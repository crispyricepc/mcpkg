import json
import re
from io import BytesIO
from pathlib import Path
from typing import Any, Optional

import requests
from colorama import Fore

from . import config, fileio
from .constants import LogLevel, PackType
from .logger import log

VT_URL = "https://vanillatweaks.net"
DP_URL = f"{VT_URL}/assets/resources/json/{config.MC_BASE_VERSION}/dpcategories.json"
CT_URL = f"{VT_URL}/assets/resources/json/{config.MC_BASE_VERSION}/ctcategories.json"
PACK_DB = config.CONFIG_DIR / "packdb.json"

pack_data = {}


def formalise_name(name: str):
    """
    Removes spaces from a name. Also adds a source identifier i.e. 'back to blocks' becomes 'VanillaTweaks.BackToBlocks'
    """
    if not name:
        log("Can't formalise name because there isn't one", LogLevel.ERROR)
        raise SystemExit(-1)
    if name_is_formalised(name):
        return name
    return f"VanillaTweaks.{name.title().replace(' ', '')}"


def name_is_formalised(name: str) -> bool:
    return "VanillaTweaks." in name


def make_post(url: str, request: dict[str, str]) -> str:
    # Prep the request (allows for more verbose debug output)
    prep_request = requests.Request('POST',
                                    url,
                                    data=request).prepare()
    log(f"Sending request: '{prep_request.body}'' to '{url}'", LogLevel.DEBUG)
    log(f"Request payload: {request}", LogLevel.DEBUG)

    s = requests.Session()
    res = s.send(prep_request)

    response_message = json.loads(res.text)
    if response_message["status"] == "error":
        log(f"""Couldn't get packs!
\tResponse from {url}: {response_message['message']}
\tFor more details, it's recommended to turn on verbose mode with either the --verbose flag or setting mcpkg.config.verbose = True""",
            LogLevel.ERROR)
        raise SystemExit(-1)

    return f"{VT_URL}{response_message['link']}"


def post_pack_dl_request(pack_ids: list[str]):
    """
    Makes a POST request to the Vanilla Tweaks server.
    Returns a URL that should be used to download the packs
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
    request_datapacks = {}
    request_craftingtweaks = []
    response_links = {}

    for pack_id in pack_ids:
        pack = get_pack_metadata(pack_id)
        category_name = pack["tags"][0].lower()

        if pack["type"] == "datapack":
            if category_name not in request_datapacks:
                request_datapacks[category_name] = []
            request_datapacks[category_name].append(pack["remoteName"])
        else:
            # Make individual requests so the VT server doesn't bundle the packs
            url = f"{VT_URL}/assets/server/zipcraftingtweaks.php"
            ct_request = {category_name: [pack["remoteName"]]}
            request_data = {
                "packs": json.dumps(ct_request),
                "version": "1.16"
            }
            if not response_links.get(PackType.CRAFTING):
                response_links[PackType.CRAFTING] = []
            response_links[PackType.CRAFTING].append({
                pack_id: make_post(url, request_data)
            })

    # Request data packs
    if request_datapacks:
        url = f"{VT_URL}/assets/server/zipdatapacks.php"
        request_data = {
            "packs": json.dumps(request_datapacks),
            "version": "1.16"
        }
        response_links[PackType.DATA] = make_post(url, request_data)

    # Request crafting tweaks
    if request_craftingtweaks:
        for ct_request in request_craftingtweaks:
            ...

    return response_links


def vt_to_packdb(src: BytesIO, dst: Path, pack_type: PackType) -> None:
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
                "tags": tags,
                "type": pack_type
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
    vt_to_packdb(datapack_metadata, PACK_DB, PackType.DATA)
    tweak_metadata = fileio.dl_with_progress(CT_URL,
                                             f"[{Fore.GREEN}INFO{Fore.RESET}] Downloading crafting tweak metadata")
    vt_to_packdb(tweak_metadata, PACK_DB, PackType.CRAFTING)
    log("Fetch complete", LogLevel.INFO)


def get_pack_metadata(pack_id: str) -> Optional[dict[str, Any]]:
    """
    Gets the metadata of a given pack
    """
    packs = get_local_pack_list()
    if pack_id not in packs:
        log(f"The pack ID '{pack_id}' was not found in the sync database. Limited management is available", LogLevel.WARN)
        return None
    return packs[pack_id]


def get_local_pack_list(pack_filter: list[str] = None) -> dict[str, dict[str, Any]]:
    """
    Gets the local pack list, filtered by the strings in `pack_filter`
    - `pack_filter` A list of pack IDs
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
            for key in packs.keys():
                if re.search(search_term, key, re.IGNORECASE):
                    results[key] = packs[key]
    else:
        results = packs

    return results
