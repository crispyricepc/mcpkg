from sys import stderr
from colorama.ansi import Fore
import requests
from tempfile import mkdtemp
from io import BytesIO
from zipfile import ZipFile
from pathlib import Path

from . import syncdb
from .constants import LogLevel, PackType, Pattern
from .logger import log
from .pack import PackSet


def dl_with_progress(url: str, display="Downloading...") -> BytesIO:
    # Streaming, so we can iterate over the response.
    response = requests.get(url, stream=True)
    total_downloaded = 0
    total_size = int(response.headers.get('content-length', 0))
    block_size = 1024  # 1 Kibibyte
    buffer = BytesIO()
    for data in response.iter_content(block_size):
        total_downloaded += len(data)
        print(
            f"[{Fore.GREEN}INFO{Fore.RESET}] {display} [{round((total_downloaded / total_size) * 100, 1)}%]\r", end="", flush=True, file=stderr)
        buffer.write(data)
    buffer.seek(0)
    print()
    log(f"'{url}' saved to memory, status code {response.status_code}", LogLevel.DEBUG)
    return buffer


def separate_datapacks(src_file: BytesIO) -> "dict[PackSet, Path]":
    """
    Separates a single zip file into their stored packs
    """
    log("Opening zip file", LogLevel.DEBUG)
    with ZipFile(src_file, "r") as zip:
        # Extract zip to temporary directory
        tmploc = Path(mkdtemp())
        zip.extractall(tmploc)

        # If datapacks, the child objects of the zip should be more zip files
        output_zips = tmploc.glob("*.zip")
        output_packs: "dict[PackSet, Path]" = {}

        for pack_zip in output_zips:
            match = Pattern.DATAPACK.match(pack_zip.stem)
            if not match:
                log(f"Regex match for '{pack_zip.stem}' failed",
                    LogLevel.ERROR)
                raise SystemExit(-1)
            # Get pack metadata info from syncdb
            pack = syncdb.get_pack_metadata(match.group("name"))
            if not pack:
                log(f"Couldn't find '{match.group('name')}' in syncdb",
                    LogLevel.ERROR)
                raise SystemExit(-1)

            # Overwrite the database's version with ours
            pack.version = match.group("version")
            o_packset = PackSet()
            o_packset[pack.id] = pack
            output_packs[o_packset] = pack_zip

    return output_packs


def move_to_disk(src_bytes: BytesIO, pack_set: PackSet) -> "dict[PackSet, Path]":
    """
    Moves the bytes given from memory onto disk, separate the packs if necessary
    """
    log("Moving packs from memory to disk", LogLevel.DEBUG)

    packs: dict[PackSet, Path] = {}

    # If type is datapack, separate into separate packs
    if len(pack_set) != 0 and pack_set.to_list()[0].pack_type == PackType.DATA:
        packs = separate_datapacks(src_bytes)

    else:
        file_path = Path(mkdtemp()) / "packs.zip"
        with open(file_path, "wb") as file:
            file.write(src_bytes.read())
        packs[pack_set] = file_path

    return packs
