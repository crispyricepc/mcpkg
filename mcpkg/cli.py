from typing import Any
from . import config
from . import syncdb
from .logger import log

import argparse
from colorama import Fore
import re


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
    list_parser.add_argument("-c", "--compact", help="compact output",
                             action="store_true")

    search_parser = subparsers.add_parser("search",
                                          help="search for a package")
    search_parser.add_argument("expression", help="the package query")


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


def list(compact: bool):
    packlist = syncdb.get_local_pack_list()
    log("Listing packs:", "info")
    for packname in packlist.keys():
        print_pack(packlist[packname], packname, compact)


def search(expression: str):
    packlist = syncdb.get_local_pack_list()
    for packname in packlist.keys():
        if re.search(expression, packname):
            print_pack(packlist[packname], packname, False)


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
        list(args.compact)
    elif args.command == "search":
        search(args.expression)
