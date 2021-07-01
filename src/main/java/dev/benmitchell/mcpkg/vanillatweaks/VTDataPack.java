package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTDataPack extends VTPack {
    public VTDataPack(JSONObject jObject, String category) {
        super(jObject, PackType.DATAPACK, category);
    }

    @Override
    public void installTo(Path destination) throws IOException, PackNotDownloadedException {
        if (!isDownloaded())
            throw new PackNotDownloadedException(this);

        File newZipLoc = Files.createTempFile("mcpkg", ".zip").toFile();

        try (ZipFile zf = new ZipFile(downloadedData)) {
            for (ZipEntry entry : Collections.list(zf.entries()))
                try (InputStream zis = zf.getInputStream(entry); OutputStream zos = new FileOutputStream(newZipLoc)) {
                    zis.transferTo(zos);
                }
        }

        setDownloadedData(newZipLoc);
        super.installTo(destination);
    }

    @Override
    public void install() throws IOException, InvalidDirectoryException, PackNotDownloadedException {
        installTo(Platform.getDataPacksDir());
    }
}
