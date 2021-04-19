from typing import Optional
from mcpkg import config
from pathlib import Path
import shutil

from .pack import Pack, PackSet, decode_packset, encode_packset
from .constants import LogLevel, PackType
from .logger import log


WORLD_FILES = (
    "advancements", "data", "datapacks", "level.dat", "playerdata", "region", "stats"
)
RESOURCEPACKS_DIR = config.MC_FOLDER / "resourcepacks"


def directory_is_a_world(directory: Path) -> bool:
    """Returns true if the given directory is a Minecraft world"""
    # Considered the traits that are necessary for a Minecraft world
    return all((directory / path).exists() for path in WORLD_FILES)


def directory_has_datapacks(directory: Path) -> bool:
    return (directory / "eula.txt").exists() and directory_is_a_world(directory / "world") or directory_is_a_world(directory) or directory.name == "datapacks" and directory_is_a_world(directory.parent)


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
        log(
            f"A datapacks directory could not be found in the given directory: '{directory}'", LogLevel.ERROR)
        raise SystemExit(-1)


def get_installed_packs(directory: Optional[Path] = None) -> PackSet:
    """
    Returns a dictionary of packs installed to the world in the given directory
    """
    packs_dirs = [RESOURCEPACKS_DIR]
    result = PackSet()
    if directory and directory_has_datapacks(directory):
        # All types of packs
        packs_dirs.append(get_datapacks_dir(directory))

    for packs_dir in packs_dirs:
        for pack_type in [PackType.RESOURCE, PackType.DATA, PackType.CRAFTING]:
            packs_file = packs_dir / f".{pack_type}s.json"

            if not packs_file.exists():
                continue

            with packs_file.open() as file:
                result.union(decode_packset(file))

    return result


def install_pack_group(source_zip: Path, dest_dir: Path, packs: PackSet, pack_type: PackType, noconfirm=False):
    """
    Installs a pre-downloaded zipped group of packs of a particular type
    - `source_zip`: A path pointing to the packs to install
    - `dest_dir`:   Any directory that can be identified by this module
                    (doesn't have to be the exact datapacks folder)
    """
    if pack_type == PackType.RESOURCE:
        pack_dir = RESOURCEPACKS_DIR
    else:
        pack_dir = get_datapacks_dir(dest_dir)
    installed_pack_path = (
        pack_dir / f"VanillaTweaks.{pack_type.display_id()}.zip")
    shutil.copy(source_zip, installed_pack_path)

    packs_file = pack_dir / f".{pack_type}s.json"
    with packs_file.open("w") as file:
        encode_packset(packs, file)

    log(f"Installed pack group of type {pack_type.display_id()}", LogLevel.INFO)


def remove_pack(pack: Pack, directory: Path):
    """
    Removes a pack from a world
    - `pack`: The pack to remove
    - `directory`: Any directory that can be identified by this module
    """
    installed_packs = get_installed_packs(directory)

    # Handle possible errors
    installed_pack = installed_packs.get(pack.id)
    if not installed_pack:
        log(f"Pack '{pack.id}' is not installed to '{directory}'",
            LogLevel.ERROR)
        raise SystemExit(-1)
    if not installed_pack.installed:
        log(f"Pack '{pack.id}' was found, but couldn't get the installed location", LogLevel.ERROR)
        raise SystemExit(-1)

    log(f"Removing '{installed_pack.id}'", LogLevel.INFO)

    log(f"Removing '{installed_pack.installed}'", LogLevel.DEBUG)
    installed_pack.installed.unlink()

    log(f"Removing managed entry for '{installed_pack.id}'", LogLevel.DEBUG)
    del installed_packs[installed_pack.id]
    # Write changes
    with (get_datapacks_dir(directory) / ".packs.json").open("w") as fp:
        encode_packset(installed_packs, fp)
