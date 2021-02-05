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

to get the virtual environment path. Then edit `.vscode/settings.json`:

```json
"python.pythonPath": "<output of poetry env info -p>/bin/python3",
"python.analysis.stubPath": "<output of poetry env info -p>/lib/python3.9/site-packages",
```
