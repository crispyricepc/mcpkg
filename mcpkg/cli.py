import argparse
import re
from pathlib import Path
from typing import Any

from colorama import Fore

from . import config, syncdb, worldmanager
from .constants import LogLevel
from .logger import log


def add_subs(parser: argparse.ArgumentParser):
    subparsers = parser.add_subparsers(dest="command")

    install_parser = subparsers.add_parser("install",
                                           help="installs a new package")
    install_parser.add_argument("pkgname", help="the name of the package")

    subparsers.add_parser("update",
                          help="updates the package database from Vanilla Tweaks")

    upgrade_parser = subparsers.add_parser("upgrade",
                                           help="upgrades a package or packages")
    upgrade_parser.add_argument("pkgname", help="the name of the package")

    list_parser = subparsers.add_parser("list", help="lists packages")
    list_parser.add_argument("-c", "--compact",
                             help="compact output",
                             action="store_true")
    list_parser.add_argument("-i", "--installed",
                             help="filter to only installed",
                             action="store_true")
    list_parser.add_argument("-p", "--path",
                             help="specify path")

    search_parser = subparsers.add_parser("search",
                                          help="search for a package")
    search_parser.add_argument("expression", help="the package query")
    search_parser.add_argument("-c", "--compact",
                               help="compact output",
                               action="store_true")


def add_opts(parser: argparse.ArgumentParser):
    parser.add_argument("-v", "--verbose", help="increase output verbosity",
                        action="store_true")


def print_pack(pack: dict[str, Any], packname: str, compact: bool):
    print(
        f"{Fore.BLUE}{pack['display']}{Fore.RESET} ({Fore.GREEN}{packname}{Fore.RESET}) v.{pack['version']}")
    if not compact:
        print(f"\t{pack['description']}")


def install():
    pass


def update():
    syncdb.fetch_pack_list()


def upgrade():
    pass


def list(compact: bool, installed: bool, path: Path = Path.cwd()):
    log("Listing packs:", LogLevel.INFO)
    pack_filter = None
    if installed:
        pack_filter = worldmanager.get_installed_packs(path)
    packlist = syncdb.get_local_pack_list(pack_filter=pack_filter)
    for packname in packlist.keys():
        print_pack(packlist[packname], packname, compact)


def search(expression: str, compact: bool):
    log("Searching:", LogLevel.INFO)
    packlist = syncdb.get_local_pack_list([expression])
    for packname in packlist.keys():
        print_pack(packlist[packname], packname, compact)


def main():
    parser = argparse.ArgumentParser(prog="mcpkg")
    add_subs(parser)
    add_opts(parser)
    args = parser.parse_args()

    # Verbose flag
    if args.verbose:
        config.verbose = True

    # Switch args
    if args.command == "install":
        install()
    elif args.command == "update":
        update()
    elif args.command == "upgrade":
        upgrade()
    elif args.command == "list":
        if args.path:
            list(args.compact, True, Path(args.path))
        else:
            list(args.compact, args.installed)
    elif args.command == "search":
        search(args.expression, args.compact)
