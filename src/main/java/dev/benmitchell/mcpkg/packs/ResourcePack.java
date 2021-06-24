package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.nio.file.Path;

import dev.benmitchell.mcpkg.Platform;

public abstract class ResourcePack implements Pack {
    @Override
    public void installTo(Path destination) {
        File downloaded = getDownloadedData();
        downloaded.renameTo(destination.resolve(getPackId() + ".zip").toFile());
    }

    public void install() {
        installTo(Platform.getResourcePacksDir());
    }
}
