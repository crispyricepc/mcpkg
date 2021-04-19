"""Constants and Enums for use in the project."""

from enum import Enum
import re

from colorama import Fore


class LogLevel(Enum):
    DEBUG = f"[{Fore.BLUE}DEBUG{Fore.RESET}]"
    INFO = f"[{Fore.GREEN}INFO{Fore.RESET}]"
    WARN = f"[{Fore.YELLOW}WARN{Fore.RESET}]"
    ERROR = f"[{Fore.RED}ERROR{Fore.RESET}]"


class PackType(str, Enum):
    RESOURCE = "resourcepack"
    DATA = "datapack"
    CRAFTING = "craftingtweak"

    def display_id(self):
        if self == PackType.RESOURCE:
            return "ResourcePacks"
        if self == PackType.DATA:
            return "DataPacks"
        if self == PackType.CRAFTING:
            return "CraftingTweaks"


class Pattern:
    DATAPACK = re.compile(r"^(?P<name>.*) v(?P<version>\d+\.\d+\.\d+).*$")
