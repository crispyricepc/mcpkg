from typing import Any, Iterable, Optional
import requests
from tempfile import mkdtemp, mkstemp
from io import BytesIO
from zipfile import ZipFile
from pathlib import Path
from colorama import Fore
from tqdm import tqdm

from .constants import LogLevel
from .logger import log


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


def separate_datapacks(src_file: BytesIO) -> Iterable[Path]:
    """
    Separates a single zip file into their stored packs
    """
    log("Opening zip file", LogLevel.DEBUG)
    with ZipFile(src_file, "r") as zip:
        tmploc = Path(mkdtemp())
        zip.extractall(tmploc)
        return tmploc.glob("*.zip")


def separate_craftingtweak(src_file: BytesIO, pack_id: Optional[str] = None, pack_version: str = "0.0.0") -> Iterable[Path]:
    """
    Has the same signature as `separate_datapacks`, will most likely only move the file from memory to disk
    """
    log("Moving crafting tweak from memory to disk", LogLevel.DEBUG)
    file_path = Path(mkdtemp()) / f"{pack_id} v{pack_version}.zip"
    with open(file_path, "wb") as file:
        file.write(src_file.read())
    return [Path(file_path)]
