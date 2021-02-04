import sys
import os
from pathlib import Path

# Linux
if sys.platform == "linux":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".config/mcpacman"
# MacOS
elif sys.platform == "darwin":
    CONFIG_DIR = Path(str(os.getenv("HOME"))) / ".mcpacman"
# Windows
elif sys.platform == "win32" or sys.platform == "cygwin" or sys.platform == "mysys":
    CONFIG_DIR = Path(str(os.getenv("APPDATA"))) / "mcpacman"

# Create the config directory
CONFIG_DIR.mkdir(parents=True, exist_ok=True)

MC_BASE_VERSION = "1.16"

verbose = False
