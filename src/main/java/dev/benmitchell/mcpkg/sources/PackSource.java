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
    public List<Pack> getPacks(List<String> packIds) throws IOException {
        List<Pack> packsToReturn = new ArrayList<Pack>();
        for (Pack pack : getPacks()) {
            if (packIds.contains(pack.getPackId()))
                packsToReturn.add(pack);
        }
        return packsToReturn;
    }

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
}
