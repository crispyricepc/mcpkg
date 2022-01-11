package dev.benmitchell.mcpkg.sources;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.exceptions.PackNotFoundException;
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

    /**
     * Checks for updates on the given packs (ignores any given packs that don't exist on the
     * remote)
     * 
     * @param packs The pacsk to check for updates with
     * @return A subset of the given packs that can be updated (with the new version)
     * @throws IOException
     */
    public List<Pack> getUpdatable(List<Pack> packs) throws IOException {
        List<Pack> packsToReturn = new ArrayList<Pack>();
        for (Pack pack : packs) {
            // Get the remote version of the pack,
            // skipping if that pack couldn't be found
            try {
                Pack packToCompareTo = getPack(pack.getPackId());
                if (pack.getVersion().compareTo(packToCompareTo.getVersion()) < 0)
                    // Update this pack
                    packsToReturn.add(packToCompareTo);
            } catch (PackNotFoundException ex) {
                MCPKGLogger.log(Level.WARNING, ex.getMessage() + ". Skipping");
            }
        }
        return packsToReturn;
    }
}
