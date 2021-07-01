package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;

public abstract class VTPack implements Pack {
    private String packId;
    private PackType packType;
    protected File downloadedData;
    private String display;
    private String description;
    private List<String> incompatible;

    // Vanilla tweaks remote data
    private String name;
    private String category;

    public VTPack(JSONObject jObject, PackType pType, String category) {
        name = (String) jObject.get("name");
        display = (String) jObject.get("display");
        description = (String) jObject.get("description");
        incompatible = new ArrayList<String>();
        for (Object item : (JSONArray) jObject.get("incompatible")) {
            incompatible.add((String) item);
        }

        packId = "VanillaTweaks." + name;
        this.category = category;
        downloadedData = null;
        packType = pType;
    }

    public void deleteDownloadedData() {
        try {
            Files.delete(downloadedData.toPath());
            downloadedData = null;
        } catch (IOException ex) {
            MCPKGLogger.log(Level.WARNING,
                    "Failed to delete downloaded cache for '" + getDisplayName() + "''. Reason: " + ex.getMessage());
        }
    }

    @Override
    public String getPackId() {
        return packId;
    }

    @Override
    public String getDisplayName() {
        return display;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getDependencies() {
        return new ArrayList<String>(0);
    }

    @Override
    public List<String> getIncompatibilities() {
        return incompatible;
    }

    @Override
    public boolean isDownloaded() {
        return downloadedData != null && downloadedData.exists();
    }

    @Override
    public PackType getPackType() {
        return packType;
    }

    @Override
    public File getDownloadedData() {
        return downloadedData;
    }

    /**
     * Sets the pack to downloaded, with the data being stored in the location at
     * downloadedData
     */
    public void setDownloadedData(File downloadedData) {
        this.downloadedData = downloadedData;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void installTo(Path destination) throws IOException, PackNotDownloadedException {
        if (!isDownloaded())
            throw new PackNotDownloadedException(this);

        downloadedData = Files.copy(downloadedData.toPath(), destination.resolve(getPackId() + ".zip"),
                StandardCopyOption.REPLACE_EXISTING).toFile();
    }
}
