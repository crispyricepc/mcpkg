package dev.benmitchell.mcpkg.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public abstract class PackSource {
    /**
     * Gets a list of all packs contained by the source
     */
    public abstract List<Pack> getPacks() throws IOException;

    /**
     * Gets a list of one or many packs based on the given pack IDs
     */
    public abstract List<Pack> getPacks(List<String> packIds) throws IOException;

    /**
     * Searches for packs within the source
     * 
     * @param keywords Keywords / strings of characters that the results could
     *                 contain
     * @return The results of the search
     */
    public List<Pack> searchForPacks(List<String> keywords) throws IOException {
        List<Pack> packsToReturn = new ArrayList<Pack>();

        for (Pack pack : getPacks()) {
            for (String keyword : keywords) {
                if (pack.getPackId().toLowerCase().contains(keyword.toLowerCase())
                        || pack.getDisplayName().toLowerCase().contains(keyword.toLowerCase())) {
                    packsToReturn.add(pack);
                }
            }
        }

        return packsToReturn;
    }

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
