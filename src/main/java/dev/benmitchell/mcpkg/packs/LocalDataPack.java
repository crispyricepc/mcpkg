package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.io.IOException;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;

/**
 * Represents a locally stored data pack
 */
public class LocalDataPack extends LocalPack {
    public LocalDataPack(String packId, Version version, File downloadedData) {
        super(packId, version, downloadedData, PackType.DATAPACK);
    }

    @Override
    public void install()
            throws IOException, InvalidDirectoryException, PackNotDownloadedException {
        installTo(Platform.getDataPacksDir());
    }
}
