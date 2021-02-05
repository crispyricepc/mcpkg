# mcpkg

Package manager for Minecraft datapacks and resourcepacks

## Installation

### The cross-platform way

The easiset and most recommended way to install mcpkg is with `pip`

```sh
pip install mcpkg
```

## Developing on VSCode

mcpkg uses poetry; a Python virtual environment. To make sure you're running in the virtual environment, run:

```sh
poetry env info -p
```

to get the virtual environment path. Press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>P</kbd> to open the command prompt and run `Python: Select Interpreter` from the options. Paste the virtual environment path into the text box with `/bin/python3` appended. Now you should be running in this virtual environment.
