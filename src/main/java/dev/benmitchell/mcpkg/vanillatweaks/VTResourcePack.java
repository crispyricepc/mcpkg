package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.IOException;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource.VTJson.Category.RemotePack;

public class VTResourcePack extends VTPack {
    public VTResourcePack(RemotePack jsonPack, String category) {
        super(jsonPack, PackType.RESOURCEPACK, category);
    }

    @Override
    public void install()
            throws IOException, InvalidDirectoryException, PackNotDownloadedException {
        installTo(Platform.getResourcePacksDir());
    }
}
