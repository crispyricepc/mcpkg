package dev.benmitchell.mcpkg.sources;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.exceptions.PackNotFoundException;
import dev.benmitchell.mcpkg.packs.Pack;

public abstract class PackSource {
    public boolean hasPack(String packId) throws IOException {
        for (Pack pack : getPacks()) {
            if (pack.getPackId().equals(packId))
                return true;
        }
        return false;
    }

    public boolean hasPack(Pack pack) throws IOException {
        return hasPack(pack.getPackId());
    }

    /**
     * Gets a list of all packs contained by the source
     */
    public abstract List<Pack> getPacks() throws IOException;

    /**
     * Gets a list of one or many packs based on the given pack IDs
     */
    public List<Pack> getPacks(List<String> packIds) throws IOException {
        List<Pack> packsToReturn = new ArrayList<Pack>();
        for (String id : packIds) {
            try {
                packsToReturn.add(getPack(id));
            } catch (PackNotFoundException ex) {
                MCPKGLogger.log(Level.WARNING, ex.getMessage() + ". Skipping...");
            }
        }
        return packsToReturn;
    }

    public Pack getPack(String packId) throws IOException, PackNotFoundException {
        String packIdLower = packId.toLowerCase();
        for (Pack pack : getPacks()) {
            if (pack.getPackId().toLowerCase().equals(packIdLower))
                return pack;

            String[] split = pack.getPackId().split("\\.");
            if (split[split.length - 1].toLowerCase().equals(packIdLower))
                return pack;
        }
        throw new PackNotFoundException(packId);
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
