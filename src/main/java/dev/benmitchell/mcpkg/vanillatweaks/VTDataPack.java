package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTDataPack extends VTPack {
    public VTDataPack(JSONObject jObject, String category) {
        super(jObject, PackType.DATAPACK, category);
    }

    @Override
    public void installTo(Path destination) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(downloadedData.toPath(), null)) {
            for (Path path : fs.getRootDirectories())
                Files.copy(path, destination.resolve(getPackId() + ".zip"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void install() throws IOException, InvalidDirectoryException {
        installTo(Platform.getDataPacksDir());

    }
}
