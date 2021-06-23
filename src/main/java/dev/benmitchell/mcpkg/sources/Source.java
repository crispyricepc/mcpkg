package dev.benmitchell.mcpkg.sources;

import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public abstract class Source {
    public abstract List<Pack> getPacks(List<String> packIds);

    public abstract List<Pack> searchForPacks(List<String> keywords);

    public abstract List<Pack> downloadPacks(List<Pack> packs);

    public List<Pack> downloadPacksFromIds(List<String> packIds) {
        return downloadPacks(getPacks(packIds));
    }
}
