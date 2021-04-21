"""Command-line package manager for Minecraft datapacks.

Usage:
  mcpkg [-v] update
  mcpkg [-v] install <packs>... [--path=<path>]
  mcpkg [-v] remove <packs>... [--path=<path>]
  mcpkg [-v] upgrade [<packs>...] [-f] [--path=<path>]
  mcpkg [-v] list [-ci] [--path=<path>]
  mcpkg [-v] search [-ci] [--path=<path>] <pattern>...

  mcpkg -h | --help
  mcpkg --version | -V

Options:
  -h --help       Show this screen.
  -v --verbose    Increase verbosity.
  -V --version    Show the current version.
  -i --installed  Only list installed packages.
  -c --compact    Display output in compact, non coloured mode.
  -f --force      Force install a package
  --path=<path>   Specify the path to a world, or datapacks folder.
"""

from pathlib import Path

from colorama import Fore
from docopt import docopt
from packaging import version
from pkg_resources import get_distribution
import os

from . import config, syncdb, worldmanager, fileio
from .constants import LogLevel, PackType
from .logger import log
from .pack import Pack, PackSet, pack_filter_str, pack_filter_type, pack_match_str


__version__ = get_distribution("mcpkg").version
arguments = docopt(__doc__, version=f"mcpkg {__version__}")


def print_pack(pack: Pack, compact: bool, colour: bool) -> None:
    """Print the name and description for the provided pack."""
    blue = green = ""
    if colour:
        blue, green = Fore.BLUE, Fore.GREEN

    description = pack.description if pack.description else "No description available"
    version = f" v.{pack.version}" if pack.version != "0.0.0" else ""

    print(f"{blue}{pack.display}{Fore.RESET} ({green}{pack.id}{Fore.RESET}){version}")
    if not compact:
        page_width = os.get_terminal_size().columns - 6
        for i in range(0, len(description), page_width):
            print(f"{' ' * 6}{description[i:i + page_width]}")


def install_packs(pack_set: PackSet, directory: Path, noconfirm=False):
    # Merge with currently installed packs
    pack_set.union(worldmanager.get_installed_packs(directory))

    # Split into pack types
    for pack_type in [PackType.DATA, PackType.CRAFTING, PackType.RESOURCE]:
        # subset = Only the packs of pack_type
        subset = pack_filter_type(pack_set, [pack_type])

        # Skip if there are no packs in the subset
        if len(subset) == 0:
            continue

        # Get the download url from vanillatweaks.net
        dl_url = syncdb.post_pack_dl_request(subset, pack_type)
        log(f"Got '{dl_url}'", LogLevel.DEBUG)

        # Download the file
        bytes = fileio.dl_with_progress(
            dl_url, f"Downloading {pack_type.display_id()}...")

        # Move the bytes to disk, also run any pack specific scripts in here
        pack_zips = fileio.move_to_disk(bytes, subset)
        for zip_set in pack_zips.keys():
            worldmanager.install_pack_group(
                pack_zips[zip_set], directory, zip_set, pack_type, noconfirm)


def install(expressions: "list[str]", directory: Path):
    log("Getting pack metadata...", LogLevel.INFO)
    packs = syncdb.get_local_pack_list()
    packs_to_install = PackSet()

    for expr in expressions:
        log(f"Searching for packs matching the expression '{expr}'",
            LogLevel.DEBUG)
        pack = pack_match_str(packs, expr)
        if pack:
            packs_to_install[pack.id] = pack

    install_packs(packs_to_install, directory)


def remove_packs(expressions: "list[str]", directory: Path):
    installed_packs = worldmanager.get_installed_packs(directory)
    for search_term in expressions:
        pack = installed_packs.get(search_term)
        if pack:
            worldmanager.remove_pack(pack, directory)
            continue


def update():
    syncdb.fetch_pack_list()


def upgrade(packs: "list[str]", force: bool, directory: Path):
    installed_packs = worldmanager.get_installed_packs(directory)
    if packs:
        installed_packs = pack_filter_str(installed_packs, packs)

    # Get full list of packs
    packs_to_upgrade = pack_filter_str(syncdb.get_local_pack_list(), packs)
    packs_to_upgrade.union(installed_packs)

    # Install them the usual way
    install_packs(packs_to_upgrade, directory, True)


def list_packages(compact: bool, installed: bool, directory: Path):
    if not (config.IS_TTY or compact):
        compact = True
        log("Pipe detected. Using compact layout", LogLevel.WARN)

    log("Listing packs:", LogLevel.INFO)
    if installed:
        pack_set = worldmanager.get_installed_packs(directory)
    else:
        pack_set = syncdb.get_local_pack_list()

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
        log("Run 'mcpkg upgrade' to update packs in this world", LogLevel.INFO)


def search(expressions: "list[str]", compact: bool, installed: bool, directory: Path):
    log("Searching:", LogLevel.INFO)
    if not installed:
        packs = syncdb.get_local_pack_list()
    else:
        packs = worldmanager.get_installed_packs()

    pack_set = pack_filter_str(packs, expressions)
    for pack in pack_set:
        print_pack(pack, compact, config.IS_TTY)


def main() -> None:
    """Entry point for the command-line script."""
    config.verbose = arguments["--verbose"]
    compact = arguments["--compact"]
    installed = arguments["--installed"]
    force = arguments["--force"]

    path = arguments["--path"]
    if not path:
        path = str(Path.cwd().absolute())

    if arguments["install"]:
        install(arguments["<packs>"], Path(path))

    elif arguments["remove"]:
        remove_packs(arguments["<packs>"], Path(path))

    elif arguments["update"]:
        update()

    elif arguments["upgrade"]:
        upgrade(arguments["<packs>"], force, Path(path))

    elif arguments["list"]:
        if arguments["--path"]:
            list_packages(compact, True, Path(path))
        else:
            list_packages(compact, installed, Path(path))

    elif arguments["search"]:
        search(arguments["<pattern>"], compact, installed, Path(path))
