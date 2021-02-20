"""Command-line package manager for Minecraft datapacks.

Usage:
  mcpkg [-v] update
  mcpkg [-v] install <packs>...
  mcpkg [-v] upgrade <packs>...
  mcpkg [-v] list [-ci] [--path=<path>]
  mcpkg [-v] search [-c] [--path=<path>] <pattern>...

  mcpkg -h | --help
  mcpkg --version | -V

Options:
  -h --help       Show this screen.
  -v --verbose    Increase verbosity.
  -V --version    Show the current version.
  -i --installed  Only list installed packages.
  -c --compact    Display output in compact, non coloured mode.
  --path=<path>   Specify the path to a world, or datapacks folder.
"""

from pathlib import Path
from typing import Optional

from colorama import Fore
from docopt import docopt
from packaging import version
from pkg_resources import get_distribution
import os

from . import config, syncdb, worldmanager, fileio
from .constants import LogLevel, PackType
from .logger import log
from .pack import Pack, PackSet


__version__ = get_distribution("mcpkg").version
arguments = docopt(__doc__, version=f"mcpkg {__version__}")


def print_pack(pack: Pack, compact: bool, colour: bool) -> None:
    """Print the name and description for the provided pack."""
    blue = green = ""
    if colour:
        blue, green = Fore.BLUE, Fore.GREEN

    description = pack.description if pack.description else "No description available"

    print(f"{blue}{pack.display}{Fore.RESET} ({green}{pack.id}{Fore.RESET}) v.{pack.version}")
    if not compact:
        page_width = os.get_terminal_size().columns - 6
        for i in range(0, len(description), page_width):
            print(f"{' ' * 6}{description[i:i + page_width]}")


def install_packs_from_url(packs_url: str, pack_type: PackType, directory: Path, pack: Optional[Pack] = None):
    bytes = fileio.dl_with_progress(packs_url, "Downloading packs")
    if pack_type == PackType.DATA:
        pack_zips = fileio.separate_datapacks(bytes)
    elif pack_type == PackType.CRAFTING:
        if not pack:
            log("Submit this issue, this code should never be reached", LogLevel.ERROR)
            raise Exception()
        pack_zips = {pack: fileio.separate_craftingtweak(bytes, pack)}
    else:
        log(f"The pack type '{pack_type}' is not currently supported",
            LogLevel.ERROR)
        raise SystemExit(-1)

    for pack_zip_metadata in pack_zips.keys():
        worldmanager.install_pack(
            pack_zips[pack_zip_metadata],
            directory,
            pack_zip_metadata
        )


def install(expressions: list[str], directory=Path.cwd()):
    log("Getting pack metadata...", LogLevel.INFO)
    packs = syncdb.get_local_pack_list(expressions)

    dl_urls = syncdb.post_pack_dl_request(packs)
    log(f"Got '{dl_urls}'", LogLevel.DEBUG)

    for url_packtype in dl_urls.keys():
        # Crafting Tweaks
        if url_packtype == PackType.CRAFTING:
            for url in dl_urls[url_packtype]:
                pack_id = list(url.keys())[0]
                pack = syncdb.get_pack_metadata(pack_id)
                install_packs_from_url(
                    url[pack_id], url_packtype, directory, pack)

        # Datapacks
        elif url_packtype == PackType.DATA:
            install_packs_from_url(
                dl_urls[url_packtype], url_packtype, directory)

        # Resource packs (not yet implemented)
        else:
            log(f"The pack type '{url_packtype}' is not yet implemented",
                LogLevel.ERROR)
            raise SystemExit(-1)


def update():
    syncdb.fetch_pack_list()


def upgrade(packs: list[str]):
    pass


def list_packages(compact: bool, installed: bool, directory=Path.cwd()):
    if not (config.IS_TTY or compact):
        compact = True
        log("Pipe detected. Using compact layout", LogLevel.WARN)

    log("Listing packs:", LogLevel.INFO)
    pack_filter = None
    pack_set = worldmanager.get_installed_packs(
        directory) if installed else syncdb.get_local_pack_list(pack_filter)

    out_of_date = PackSet()
    for pack in pack_set:
        print_pack(pack, compact, config.IS_TTY)
        # Compare the locally installed pack's version to the syncdb
        if version.parse(syncdb.get_pack_metadata(pack.id).version) > version.parse(pack.version):
            out_of_date[pack.id] = pack

    if len(out_of_date) != 0:
        for p in out_of_date:
            log(f"{Fore.GREEN}{p.id}{Fore.RESET} can be updated to {syncdb.get_pack_metadata(p.id).version}",
                LogLevel.WARN)


def search(expressions: list[str], compact: bool):
    log("Searching:", LogLevel.INFO)
    pack_set = syncdb.search_local_pack_list(expressions)
    for pack in pack_set:
        print_pack(pack, compact, config.IS_TTY)


def main() -> None:
    """Entry point for the command-line script."""
    config.verbose = arguments.get("--verbose", False)
    compact, installed = arguments["--compact"], arguments["--installed"]

    if arguments["install"]:
        install(arguments["<packs>"])

    elif arguments["update"]:
        update()

    elif arguments["upgrade"]:
        upgrade(arguments["<packs>"])

    elif arguments["list"]:
        if path := arguments["--path"]:
            list_packages(compact, installed=True, path=path)
        else:
            list_packages(compact, installed=installed)

    elif arguments["search"]:
        search(arguments["<pattern>"], compact)
