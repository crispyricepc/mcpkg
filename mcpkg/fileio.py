from .constants import LogLevel
from .logger import log

import requests
from io import BytesIO
from tqdm import tqdm
from colorama import Fore


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
