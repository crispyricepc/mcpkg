from json.decoder import JSONDecoder
from mcpkg.constants import PackType
from typing import Optional
from json import JSONEncoder


class Pack:
    def __init__(
        self,
        id: str,
        display: str,
        pack_type: PackType,
        version: str = "0.0.0",
        description: Optional[str] = None,
        tags: Optional[list[str]] = None,
        category: Optional[str] = None
    ):
        self.id = id
        self.display = display
        self.pack_type = pack_type
        self.version = version
        self.description = description
        self.tags = tags
        self.category = category

    @property
    def formal_name(self):
        """
        Removes spaces from a name. Also adds a source identifier i.e. 'back to blocks' becomes 'VanillaTweaks.BackToBlocks'
        """
        return f"VanillaTweaks.{self.id.title().replace(' ', '')}"

    @property
    def remote_name(self):
        """
        Gets the name of a pack that is used for lookups to the Vanilla Tweaks server
        """
        pass


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

# JSON Encoders / Decoders


class PackSetEncoder(JSONEncoder):
    def default(self, o: PackSet):
        return list(o._content.values())


class PackSetDecoder(JSONDecoder):
    def __init__(self, *args, **kwargs):
        JSONDecoder.__init__(
            self,
            object_hook=self.object_hook,
            *args,
            **kwargs
        )

    def object_hook(self, dct):
        print(dct)
