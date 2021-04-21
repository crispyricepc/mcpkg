import sys

from . import config
from .constants import LogLevel


def log(message: object, level: LogLevel, end="\n") -> None:
    """Log a provided message to stderr."""
    if level is LogLevel.DEBUG and not config.verbose:
        return
    print(f"{level.value} {message}", file=sys.stderr, end=end)
