# mcpkg

Package manager for Minecraft datapacks, crafting tweaks, and resource packs

![demo](images/list-install-demo.png)

## Usage

```sh
mcpkg --help
```

Will give you a list of commands that can be run. Certain commands like `mcpkg list --installed` and `mcpkg install <name>` can only be run inside of a Minecraft world folder. By default your Minecraft worlds are located in `.minecraft/saves`.

## Installation

### Dependencies

- **Java 11 or better:** If you're on linux or macOS, you should install from your package manager. If you're on Windows, you can install java from [their site](https://www.java.com/en/download/manual.jsp)

## FAQ

Answers to a few questions that may crop up regarding this tool:

### Why use a command line over a GUI app?

This project is aimed mostly at server admins, not end-users. It is very difficult to co-ordinate GUIs with Minecraft servers as they are usually on a different PC to the operator. If you are looking for a way to easily install datapacks with a graphical user interface, might I recommend the API that this tool is built around; [vanillatweaks.net](https://vanillatweaks.net/).

### How can you be sure that this project won't just die just like every package manager before?

All things tend towards entropy. But we have the advantage that the core database and logic is already maintained by a well-proven team, and is already in use by many Minecraft server owners and client users. Ideally the only way this tool will stop working, is if nobody is working on Vanilla Tweaks.

### How can I submit my own packs?

We're just the messenger. This is not the place to submit packs. Please see [vanillatweaks.net](https://vanillatweaks.net/)

### Can I help make this better?

Yes! This is GitHub, make an account if you don't have one already. Clone the repo or make an [issue](https://github.com/CRISPYricePC/mcpkg/issues/new/choose) at the top, we'll be happy to help
