package dev.benmitchell.mcpkg.sources;

import java.io.IOException;
import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public abstract class RemoteSource extends PackSource {
    /**
     * Downloads the given packs to memory, storing the results in the pack objects
     * 
     * @return The list of packs (this should be unchanged)
     */
    public abstract List<Pack> downloadPacks(List<Pack> packs) throws IOException;

    /**
     * Downloads the given packs to memory, storing the results in the pack objects
     * 
     * @see #downloadPacks(List)
     * @return The packs with their data downloaded
     */
    public List<Pack> downloadPacksFromIds(List<String> packIds) throws IOException {
        return downloadPacks(getPacks(packIds));
    }
}
