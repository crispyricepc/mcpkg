from . import config
from .logger import log

import tempfile
import requests
import json
from typing import Any
from tqdm import tqdm
from colorama import Fore
from pathlib import Path

VT_URL = "https://vanillatweaks.net/"
DP_URL = f"{VT_URL}assets/resources/json/{config.MC_BASE_VERSION}/dpcategories.json"
CT_URL = f"{VT_URL}assets/resources/json/{config.MC_BASE_VERSION}/ctcategories.json"
PACK_DB = config.CONFIG_DIR / "packdb.json"


def formalise_name(name: str):
    """
    Removes spaces from a name. Also adds a source identifier i.e. 'back to blocks' becomes 'vanillatweaks.backtoblocks'
    """


def dl_with_progress(url: str, dst: str, display: str):
    # Streaming, so we can iterate over the response.
    response = requests.get(url, stream=True)
    total_size_in_bytes = int(response.headers.get('content-length', 0))
    block_size = 1024  # 1 Kibibyte
    progress_bar = tqdm(total=total_size_in_bytes, unit="iB",
                        unit_scale=True, desc=display,
                        bar_format="{l_bar}%s{bar}%s{r_bar}" %
                        (Fore.BLUE, Fore.RESET),
                        ascii=True)
    with open(dst, 'wb') as file:
        for data in response.iter_content(block_size):
            progress_bar.update(len(data))
            file.write(data)
    progress_bar.close()
    log(f"'{url}' saved to '{dst}', status code {response.status_code}", "debug")
    if total_size_in_bytes != 0 and progress_bar.n != total_size_in_bytes:
        log("Printing the fancy progress bar caused an issue, it's probably okay to ignore this", "warn")


def vt_to_packdb(src: Path, dst: Path):
    """
    Converts a vanilla tweaks JSON metadata file into a compatible list of packs.
    - src: The path to the vanilla tweaks metadata
    - dst: The path to the new pack list (usually ~/.config/mcpacman/packs.json)
    """
    src_dict = json.load(src.open())
    new_dict = {}
    for src_category in src_dict["categories"]:
        for src_pack in src_category["packs"]:
            tags = [src_category["category"]]
            new_dict[src_pack["name"]] = {
                "display": src_pack["display"],
                "version": src_pack["version"],
                "description": src_pack["description"],
                "tags": tags
            }

    # Load any already stored values and union them
    if (dst.exists()):
        dst_dict = json.load(dst.open())
        new_dict = dst_dict | new_dict

    json.dump(new_dict, dst.open("w"), indent=2, sort_keys=True)


def fetch_pack_list():
    """
    Fetches the pack list from Vanilla Tweaks servers and stores it in a compatible pack list
    """
    file, filename = tempfile.mkstemp()
    dl_with_progress(DP_URL, filename,
                     f"[{Fore.GREEN}INFO{Fore.RESET}] Downloading datapack metadata")
    vt_to_packdb(Path(filename), PACK_DB)
    file, filename = tempfile.mkstemp()
    dl_with_progress(
        CT_URL, filename, f"[{Fore.GREEN}INFO{Fore.RESET}] Downloading crafting tweak metadata")
    vt_to_packdb(Path(filename), PACK_DB)
    log("Fetch complete", "info")


def get_local_pack_list() -> dict[str, dict[str, Any]]:
    if not PACK_DB.exists():
        log("Can't find a locally stored packdb.json. Attempting to fetch now...", "warn")
        fetch_pack_list()

    return json.load(PACK_DB.open(mode="r"))
