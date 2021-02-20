import json
from json.decoder import JSONDecoder
from mcpkg.constants import PackType
from typing import Any, Optional
from json import JSONEncoder


class Pack:
    def __init__(
        self,
        remote_name: str,
        display: str,
        pack_type: PackType,
        category: str,
        version: str = "0.0.0",
        description: Optional[str] = None,
        tags: Optional[list[str]] = None
    ):
        self.remote_name = remote_name
        self.display = display
        self.pack_type = pack_type
        self.version = version
        self.description = description
        self.tags = tags
        self.category = category

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


class PackSetEncoder(JSONEncoder):
    def default(self, o):
        if type(o) is PackSet:
            return list(o._content.values())
        elif type(o) is Pack:
            return o.__dict__
        else:
            raise TypeError(
                f"The PackSetEncoder can only encode Packs and PackSets, not {type(o).__name__}")


def decode_packset(fp):
    lst: list[Pack] = json.load(fp, cls=PackSetDecoder)
    pack_set = PackSet()
    for pack in lst:
        pack_set[pack.id] = pack
    return pack_set


class PackSetDecoder(JSONDecoder):
    def __init__(self, *args, **kwargs):
        JSONDecoder.__init__(
            self,
            object_hook=self.object_hook,
            *args,
            **kwargs
        )

    def object_hook(self, dct: dict[str, Any]):
        return Pack(
            dct["remote_name"],
            dct["display"],
            dct["pack_type"],
            dct["category"],
            dct["version"],
            dct["description"],
            dct["tags"]
        )
