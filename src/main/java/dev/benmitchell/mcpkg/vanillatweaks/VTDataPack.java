package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        try (ZipFile zf = new ZipFile(downloadedData)) {
            for (ZipEntry entry : Collections.list(zf.entries()))
                try (InputStream zis = zf.getInputStream(entry);
                        OutputStream zos = new FileOutputStream(destination.toFile())) {
                    zis.transferTo(zos);
                }
        }
    }

    @Override
    public void install() throws IOException, InvalidDirectoryException {
        installTo(Platform.getDataPacksDir().resolve(getPackId() + ".zip"));
    }
}
