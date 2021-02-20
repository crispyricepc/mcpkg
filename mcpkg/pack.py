import json
from pathlib import Path
from mcpkg.constants import PackType
from typing import Any, Optional


class Pack:
    def __init__(
        self,
        remote_name: str,
        display: str,
        pack_type: PackType,
        category: str,
        version: str = "0.0.0",
        description: Optional[str] = None,
        tags: Optional[list[str]] = None,
        installed: Optional[Path] = None
    ):
        self.remote_name = remote_name
        self.display = display
        self.pack_type = pack_type
        self.category = category
        self.version = version
        self.description = description
        self.tags = tags
        self.installed = installed

    @property
    def id(self):
        """
        Removes spaces from a name. Also adds a source identifier i.e. 'back to blocks' becomes 'VanillaTweaks.BackToBlocks'
        """
        return f"VanillaTweaks.{self.remote_name.title().replace(' ', '')}"


class PackSet:
    """
    Represents a set of packs, see `class Pack`

    A set `my_set` could be indexed using the pack id like so:
    ```
    my_pack: Pack = my_set[pack_id]
    ```

    It can also be iterated over with:
    ```
    for my_pack: Pack in my_set:
        ...
    ```
    """

    def __init__(self):
        self._content: dict[str, Pack] = {}

    def __len__(self):
        return len(self._content)

    def __getitem__(self, key: str):
        return self._content[key]

    def get(self, key: str) -> Optional[Pack]:
        return self._content.get(key)

    def __setitem__(self, key: str, value: Pack):
        self._content[key] = value

    def __delitem__(self, key: str):
        del self._content[key]

    def __iter__(self):
        return iter(self._content.values())

    def union(self, s):
        """
        Merges another packset into this one
        """
        self._content = self._content | s._content


# JSON Encoders / Decoders


def encode_packset(pack_set: PackSet, fp):
    pack_list = []
    for pack in pack_set:
        pack_dict = pack.__dict__
        pack_dict["installed"] = str(pack_dict["installed"])
        pack_list.append(pack_dict)
    json.dump(pack_list, fp)


def decode_packset(fp):
    lst: list[dict[str, Any]] = json.load(fp)
    pack_set = PackSet()
    for pack_dct in lst:
        if installed_loc := pack_dct.get("installed"):
            installed_path = Path(installed_loc)
        else:
            installed_path = None
        pack = Pack(
            pack_dct["remote_name"],
            pack_dct["display"],
            pack_dct["pack_type"],
            pack_dct["category"],
            pack_dct["version"],
            pack_dct.get("description"),
            pack_dct.get("tags"),
            installed_path
        )
        pack_set[pack.id] = pack
    return pack_set
