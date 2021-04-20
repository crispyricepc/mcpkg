import json
from mcpkg.logger import log
from pathlib import Path
from mcpkg.constants import LogLevel, PackType
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
        tags: "Optional[list[str]]" = None,
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
        return f"{PackType(self.pack_type).display_id()}.{self.remote_name.title().replace(' ', '')}"


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
        self._content: "dict[str, Pack]" = {}

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
        self._content.update(s._content)

    def to_list(self):
        """
        Convert the set to a list of packs
        """
        return list(self._content.values())


def _match_term(pack_set: PackSet, search_term: str) -> PackSet:
    results = PackSet()
    for pack in pack_set:
        if search_term.lower() in pack.id.split(".")[1].lower():
            results[pack.id] = pack

    return results


def pack_filter_str(pack_set: PackSet, pack_filter: "Optional[list[str]]") -> PackSet:
    if not pack_filter or len(pack_filter) == 0:
        return PackSet()

    results = PackSet()
    for search_term in pack_filter:
        term_results = PackSet()
        # First try to non-iteratively find
        pack = pack_set.get(search_term)
        if pack:
            term_results[search_term] = pack
            continue

        # More expensive fallback
        term_results.union(_match_term(pack_set, search_term))

        if len(term_results) == 0:
            log(f"Could not find a pack matching the expression '{search_term}'",
                LogLevel.WARN)
        else:
            results.union(term_results)

    return results


def pack_filter_type(pack_set: PackSet, filters: "list[PackType]") -> PackSet:
    results = PackSet()
    for pack in pack_set:
        if pack.pack_type in filters:
            results[pack.id] = pack

    return results

# JSON Encoders / Decoders


def encode_packset(pack_set: PackSet, fp):
    pack_list = []
    for pack in pack_set:
        pack_dict = pack.__dict__
        pack_dict["installed"] = str(pack_dict["installed"])
        pack_list.append(pack_dict)
    json.dump(pack_list, fp)


def decode_packset(fp):
    lst: "list[dict[str, Any]]" = json.load(fp)
    pack_set = PackSet()
    for pack_dct in lst:
        installed_loc = pack_dct.get("installed")
        if installed_loc:
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
