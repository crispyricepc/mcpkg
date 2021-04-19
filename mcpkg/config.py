import os
import sys
from pathlib import Path

# Linux
if sys.platform == "linux":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".config/mcpkg"
    MC_FOLDER = Path(str(os.getenv("HOME"))) / ".minecraft"
# MacOS
elif sys.platform == "darwin":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".mcpkg"
    MC_FOLDER = Path(str(os.getenv("HOME"))) / ".minecraft"
# Windows
elif sys.platform == "win32" or sys.platform == "cygwin" or sys.platform == "mysys":
    CONFIG_DIR = Path(str(os.getenv("APPDATA"))) / "mcpkg"
    MC_FOLDER = Path(str(os.getenv("APPDATA"))) / ".minecraft"

# Create the config directory
CONFIG_DIR.mkdir(parents=True, exist_ok=True)

MC_BASE_VERSION = "1.16"
IS_TTY = sys.stdout.isatty()

verbose = False
