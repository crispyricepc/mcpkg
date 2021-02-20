import json
from mcpkg.pack import Pack, PackSet, PackSetEncoder, decode_packset
from pathlib import Path
from typing import Final
from colorama import Fore
import shutil

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


def get_installed_packs(directory: Path) -> PackSet:
    """
    Returns a dictionary of packs installed to the world in the given directory
    """
    datapack_dir = get_datapacks_dir(directory)
    packs_file = datapack_dir / ".packs.json"

    if not packs_file.exists():
        log("This world has no datapacks or is not managed by the tool", LogLevel.WARN)
        return PackSet()

    with packs_file.open() as file:
        return decode_packset(file)


def install_pack(source_zip: Path, dest_dir: Path, pack: Pack):
    """
    Installs a pre-downloaded zipped pack to the destination world
    - `source_zip`: A path pointing to the pack to install
    - `dest_dir`:   Any directory that can be identified by this module
                    (doesn't have to be the exact datapacks folder)
    """
    datapack_dir = get_datapacks_dir(dest_dir)
    packs_file = datapack_dir / ".packs.json"
    installed_pack_path = (
        datapack_dir / f"{pack.id}.{pack.version}.zip")

    installed_packs = get_installed_packs(dest_dir)
    if installed_packs.get(pack.id):
        log(
            f"The pack you are trying to install ({Fore.GREEN}{pack.id}{Fore.RESET}) already exists", LogLevel.WARN)
        if not ((replace_pack := input("Replace? [y/N]: ").lower()) == "y" or replace_pack == "yes"):
            return

    log(f"Installing '{source_zip}' to '{installed_pack_path}'",
        LogLevel.DEBUG)
    shutil.copy(source_zip, installed_pack_path)

    installed_packs[pack.id] = pack

    log(f"Creating new managed entry in '{packs_file}'",
        LogLevel.DEBUG)
    with packs_file.open("w") as file:
        json.dump(installed_packs, file, cls=PackSetEncoder)

    log(
        f"Installed {Fore.GREEN}{pack.id}{Fore.RESET} v.{pack.version}", LogLevel.INFO)
