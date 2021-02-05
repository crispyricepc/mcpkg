import json
from pathlib import Path
from typing import Final

from .constants import LogLevel
from .logger import log


WORLD_FILES: Final[tuple[str, ...]] = (
    "advancements", "data", "datapacks", "level.dat", "playerdata", "region", "stats"
)


def directory_is_a_world(directory: Path) -> bool:
    """Returns true if the given directory is a Minecraft world"""
    # Considered the traits that are necessary for a Minecraft world
    return all((directory / path).exists() for path in WORLD_FILES)


def get_datapacks_dir(directory: Path) -> Path:
    """
    Returns the path to the datapacks folder for the given directory

    Raises `SystemExit` if the current working directory is not valid
    """
    # Is a server
    if (directory / "eula.txt").exists() and directory_is_a_world(directory / "world"):
        return directory / "world" / "datapacks"

    # Is the world folder
    elif directory_is_a_world(directory):
        return directory / "datapacks"

    # Is a datapacks folder
    elif directory.name == "datapacks" and directory_is_a_world(directory.parent):
        return directory

    else:
        log("A datapacks folder could not be found in the given directory", LogLevel.ERROR)
        raise SystemExit(-1)


def get_installed_packs(directory: Path) -> list[dict[str, str]]:
    """
    Returns a list of pack ids installed to the world in the given directory
    """
    datapack_dir = get_datapacks_dir(directory)
    packs_file = datapack_dir / ".packs.json"

    if not packs_file.exists():
        log("This world has no datapacks or is not managed by the tool", LogLevel.WARN)
        return []

    with packs_file.open() as file:
        return json.load(file)


def install_pack(source_zip: Path, dest_dir: Path, pack_id: str, version: str):
    """
    Installs a pre-downloaded zipped pack to the destination world
    - `source_zip`: A path pointing to the pack to install
    - `dest_dir`:   Any directory that can be identified by this module
                    (doesn't have to be the exact datapacks folder)
    """
    datapack_dir = get_datapacks_dir(dest_dir)
    installed_pack_path = (datapack_dir /
                           f"{pack_id}.{version}.zip")

    log(f"Installing '{source_zip}' to '{installed_pack_path}'",
        LogLevel.DEBUG)
    source_zip.rename(installed_pack_path)

    log(f"Creating new managed entry in '{datapack_dir / '.packs.json'}'",
        LogLevel.DEBUG)
    installed_packs = get_installed_packs(dest_dir)
    installed_packs.append({
        "id": pack_id,
        "version": version,
        "location": str(installed_pack_path)
    })
    json.dump(installed_packs, (datapack_dir / ".packs.json").open("w"))
