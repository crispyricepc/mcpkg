package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;

public abstract class LocalPack extends Pack {
    /**
     * Creates a new pack object based on a given file
     * 
     * @param file     The file that the pack is based on
     * @param packType The type of pack
     * @return A LocalPack that's either a DataPack or a ResourcePack
     */
    public static LocalPack fromFile(File file, PackType packType) {
        String[] details = file.getName().split("\\.");
        Version version;
        if (details.length == 6)
            // If version is in the pack name
            version = new Version(Integer.parseInt(details[2]), Integer.parseInt(details[3]),
                    Integer.parseInt(details[4]));
        else
            version = new Version();
        switch (packType) {
            case DATAPACK:
            case CRAFTINGTWEAK:
                return new LocalDataPack(details[0] + "." + details[1], version, file);
            case RESOURCEPACK:
                return new LocalResourcePack(details[0] + "." + details[1], version, file);
            default:
                throw new NotImplementedException();
        }
    }

    public LocalPack(String packId, Version version, File downloadedData, PackType packType) {
        super(packId, packId.split("\\.")[1], "", version, new ArrayList<String>(), new ArrayList<String>(), packType,
                Optional.of(downloadedData));
    }
}
