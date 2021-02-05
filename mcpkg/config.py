import os
import sys
from pathlib import Path

# Linux
if sys.platform == "linux":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".config/mcpkg"
# MacOS
elif sys.platform == "darwin":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".mcpkg"
# Windows
elif sys.platform == "win32" or sys.platform == "cygwin" or sys.platform == "mysys":
    CONFIG_DIR = Path(str(os.getenv("APPDATA"))) / "mcpkg"

# Create the config directory
CONFIG_DIR.mkdir(parents=True, exist_ok=True)

MC_BASE_VERSION = "1.16"
IS_TTY = sys.stdout.isatty()

verbose = False
