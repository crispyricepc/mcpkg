import json
import os
from pathlib import Path

from .constants import LogLevel
from .logger import log


def directory_is_a_world(dir: Path) -> bool:
    """Returns true if the given directory is a Minecraft world"""
    # Considered the traits that are necessary for a Minecraft world
    return ((dir).exists()
            and (dir / "advancements").exists()
            and (dir / "data").exists()
            and (dir / "datapacks").exists()
            and (dir / "level.dat").exists()
            and (dir / "playerdata").exists()
            and (dir / "region").exists()
            and (dir / "stats").exists())


def get_datapacks_dir(dir: Path) -> Path:
    """
    Returns the path to the datapacks folder for the given directory

    Raises `SystemExit` if the current working directory is not valid
    """
    # Is a server
    if (dir / "eula.txt").exists() and directory_is_a_world(dir / "world"):
        return dir / "world" / "datapacks"

    # Is the world folder
    elif directory_is_a_world(dir):
        return dir / "datapacks"

    # Is a datapacks folder
    elif dir.name == "datapacks" and directory_is_a_world(dir.parent):
        return dir

    else:
        log("A datapacks folder could not be found in the given directory", LogLevel.ERROR)
        raise SystemExit()


def get_installed_packs(dir: Path) -> list[str]:
    """
    Returns a list of pack ids installed to the world in the given directory
    """
    datapack_dir = get_datapacks_dir(dir)
    if not (datapack_dir / ".packs.json").exists():
        log("This world has no datapacks or is not managed by the tool", LogLevel.WARN)
        return []

    return json.load((datapack_dir / ".packs.json").open())
