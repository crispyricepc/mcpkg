"""Constants and Enums for use in the project."""

from enum import Enum, auto

from colorama import Fore


class LogLevel(Enum):
    DEBUG = f"[{Fore.BLUE}DEBUG{Fore.RESET}]"
    INFO = f"[{Fore.GREEN}INFO{Fore.RESET}]"
    WARN = f"[{Fore.YELLOW}WARN{Fore.RESET}]"
    ERROR = f"[{Fore.RED}ERROR{Fore.RESET}]"
