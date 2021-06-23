package dev.benmitchell.mcpkg.sources;

import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public abstract class PackSource {
    /**
     * Gets a list of one or many packs based on the given pack IDs
     */
    public abstract List<Pack> getPacks(List<String> packIds);

    /**
     * Searches for packs within the source
     * 
     * @param keywords Keywords / strings of characters that the results could
     *                 contain
     * @return The results of the search
     */
    public abstract List<Pack> searchForPacks(List<String> keywords);

    /**
     * Downloads the given packs to memory, storing the results in the pack objects
     * 
     * @return The list of packs (this should be unchanged)
     */
    public abstract List<Pack> downloadPacks(List<Pack> packs);

    /**
     * Downloads the given packs to memory, storing the results in the pack objects
     * 
     * @see #downloadPacks(List)
     * @return The packs with their data downloaded
     */
    public List<Pack> downloadPacksFromIds(List<String> packIds) {
        return downloadPacks(getPacks(packIds));
    }
}
