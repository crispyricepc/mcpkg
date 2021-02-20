import requests
from tempfile import mkdtemp
from io import BytesIO
from zipfile import ZipFile
from pathlib import Path
from colorama import Fore
from tqdm import tqdm

from . import syncdb
from .constants import LogLevel, Pattern
from .logger import log
from .pack import Pack


def dl_with_progress(url: str, display: str) -> BytesIO:
    # Streaming, so we can iterate over the response.
    response = requests.get(url, stream=True)
    total_size_in_bytes = int(response.headers.get('content-length', 0))
    block_size = 1024  # 1 Kibibyte
    progress_bar = tqdm(total=total_size_in_bytes, unit="iB",
                        unit_scale=True, desc=display,
                        bar_format="{l_bar}%s{bar}%s{r_bar}" %
                        (Fore.BLUE, Fore.RESET),
                        ascii=True)
    buffer = BytesIO()
    for data in response.iter_content(block_size):
        progress_bar.update(len(data))
        buffer.write(data)
    buffer.seek(0)
    progress_bar.close()
    log(f"'{url}' saved to memory, status code {response.status_code}", LogLevel.DEBUG)
    if total_size_in_bytes != 0 and progress_bar.n != total_size_in_bytes:
        log("Printing the fancy progress bar caused an issue, it's probably okay to ignore this", LogLevel.WARN)
    return buffer


def separate_datapacks(src_file: BytesIO) -> dict[Pack, Path]:
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
        output_packs: dict[Pack, Path] = {}

        for pack_zip in output_zips:
            if not (match := Pattern.DATAPACK.match(pack_zip.stem)):
                log(f"Regex match for '{pack_zip.stem}' failed",
                    LogLevel.ERROR)
                raise SystemExit(-1)
            # Get pack metadata info from syncdb
            if not (pack := syncdb.get_pack_metadata(match.group("name"))):
                log(f"Couldn't find '{match.group('name')}' in syncdb",
                    LogLevel.ERROR)
                raise SystemExit(-1)

            # Overwrite the database's version with ours
            pack.version = match.group("version")
            output_packs[pack] = pack_zip

    return output_packs


def separate_craftingtweak(src_file: BytesIO, pack: Pack) -> Path:
    """
    Has the same signature as `separate_datapacks`, will most likely only move the file from memory to disk
    """
    log("Moving crafting tweak from memory to disk", LogLevel.DEBUG)
    file_path = Path(mkdtemp()) / f"{pack.id} v{pack.version}.zip"
    with open(file_path, "wb") as file:
        file.write(src_file.read())
    return Path(file_path)
