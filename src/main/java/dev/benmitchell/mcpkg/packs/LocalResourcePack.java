package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.io.IOException;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;

public class LocalResourcePack extends LocalPack {
    public LocalResourcePack(String packId, Version version, File downloadedData) {
        super(packId, version, downloadedData, PackType.RESOURCEPACK);
    }

    @Override
    public void install() throws IOException, InvalidDirectoryException, PackNotDownloadedException {
        installTo(Platform.getResourcePacksDir());
    }
}
