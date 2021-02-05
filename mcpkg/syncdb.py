import json
import re
from io import BytesIO
from pathlib import Path
from typing import Any

import requests
from colorama import Fore

from . import config, fileio
from .constants import LogLevel
from .logger import log

VT_URL = "https://vanillatweaks.net/"
DP_URL = f"{VT_URL}assets/resources/json/{config.MC_BASE_VERSION}/dpcategories.json"
CT_URL = f"{VT_URL}assets/resources/json/{config.MC_BASE_VERSION}/ctcategories.json"
PACK_DB = config.CONFIG_DIR / "packdb.json"

pack_data = {}


def formalise_name(name: str):
    """
    Removes spaces from a name. Also adds a source identifier i.e. 'back to blocks' becomes 'VanillaTweaks.BackToBlocks'
    """
    return f"VanillaTweaks.{name.title().replace(' ', '')}"


def download_packs(pack_ids: list[str]) -> list[Path]:
    """
    Downloads a group of packs.
    Zips packs into individual .zip files and returns a list of their locations on disk.
    Request structure:
    ```
      packs: {
          "category_name": [
              "remote_name_a",
              "remote_name_b"
          ]
      },
      version: 1.16
    ```
    """
    paths = []
    request_packs = {}
    for pack_id in pack_ids:
        pack = get_pack_metadata(pack_id)
        category_name = pack["tags"][0].lower()
        if category_name not in request_packs:
            request_packs[category_name] = []

        request_packs[category_name].append(pack["remoteName"])

    url = f"{VT_URL}assets/server/zipcraftingtweaks.php"
    request_data = {"packs": json.dumps(request_packs), "version": "1.16"}
    # Prep the request (allows for more verbose debug output)
    prep_request = requests.Request('POST',
                                    url,
                                    data=request_data).prepare()
    log(f"Sending request: '{prep_request.body}'' to '{url}'", LogLevel.DEBUG)
    log(f"Request payload: {request_data}", LogLevel.DEBUG)

    s = requests.Session()
    res = s.send(prep_request)

    response_message = json.loads(res.text)
    if response_message["status"] == "error":
        log(f"""Couldn't get packs!
\tResponse from {url}: {response_message['message']}
\tFor more details, it's recommended to turn on verbose mode with either the --verbose flag or setting mcpkg.config.verbose = True""",
            LogLevel.ERROR)
        raise SystemExit(-1)

    return paths


def vt_to_packdb(src: BytesIO, dst: Path) -> None:
    """
    Converts a vanilla tweaks JSON metadata file into a compatible list of packs.
    - src: The path to the vanilla tweaks metadata
    - dst: The path to the new pack list (usually ~/.config/mcpkg/packs.json)
    """
    global pack_data
    src_dict = json.load(src)
    new_dict = {}
    for src_category in src_dict["categories"]:
        for src_pack in src_category["packs"]:
            tags = [src_category["category"]]
            new_dict[formalise_name(src_pack["name"])] = {
                "remoteName": src_pack["name"],
                "display": src_pack["display"],
                "version": src_pack["version"],
                "description": src_pack["description"],
                "tags": tags
            }

    # Load any already stored values and union them
    if (dst.exists()):
        dst_dict = json.load(dst.open())
        new_dict = dst_dict | new_dict

    pack_data = new_dict
    json.dump(new_dict, dst.open("w"), indent=2, sort_keys=True)


def fetch_pack_list() -> None:
    """
    Fetches the pack list from Vanilla Tweaks servers and stores it in a compatible pack list
    """
    datapack_metadata = fileio.dl_with_progress(DP_URL,
                                                f"[{Fore.GREEN}INFO{Fore.RESET}] Downloading datapack metadata")
    vt_to_packdb(datapack_metadata, PACK_DB)
    tweak_metadata = fileio.dl_with_progress(CT_URL,
                                             f"[{Fore.GREEN}INFO{Fore.RESET}] Downloading crafting tweak metadata")
    vt_to_packdb(tweak_metadata, PACK_DB)
    log("Fetch complete", LogLevel.INFO)


def get_pack_metadata(pack_id: str) -> dict[str, Any]:
    """
    Gets the metadata of a given pack
    """
    packs = get_local_pack_list()
    if pack_id not in packs:
        log(f"The pack ID '{pack_id}' was not found in the sync database", LogLevel.ERROR)
        raise SystemExit(-1)
    return packs[pack_id]


def get_local_pack_list(pack_filter: list[dict[str, str]] = None) -> dict[str, dict[str, Any]]:
    """
    Gets the local pack list, filtered by the objects in `pack_filter`
    """
    global pack_data
    if not PACK_DB.exists():
        log("Can't find a locally stored packdb.json. Attempting to fetch now...", LogLevel.WARN)
        fetch_pack_list()

    if pack_data is not None:
        with PACK_DB.open() as file:
            packs = json.load(file)
        pack_data = packs
    else:
        packs = pack_data

    if pack_filter is not None:
        results = {}
        for search_term in pack_filter:
            expression = search_term["id"]
            for key in packs.keys():
                if re.search(expression, key, re.IGNORECASE):
                    results[key] = packs[key]
                    if "version" in search_term:
                        results[key]["version"] = search_term["version"]
    else:
        results = packs

    return results
