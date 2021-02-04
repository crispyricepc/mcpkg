from . import config
import sys
from colorama import Fore, Style


def log(msg: str, type: str):
    file = sys.stderr
    if type == "debug":
        if not config.verbose:
            return
        pre = f"[{Fore.BLUE}DEBUG{Style.RESET_ALL}]"
    elif type == "info":
        pre = f"[{Fore.GREEN}INFO{Style.RESET_ALL}]"
    elif type == "warn":
        pre = f"[{Fore.YELLOW}WARN{Style.RESET_ALL}]"
    elif type == "error":
        pre = f"[{Fore.RED}ERROR{Style.RESET_ALL}]"
    else:
        return

    print(f"{pre} {msg}", file=file)
