package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;

public abstract class VTPack implements Pack {
    private String packId;
    private boolean downloaded;
    private PackType packType;
    private ByteArrayInputStream downloadedData;
    private String display;
    private String description;
    private List<String> incompatible;

    // Vanilla tweaks remote data
    private String name;

    public VTPack(JSONObject jObject, PackType pType) {
        name = (String) jObject.get("name");
        display = (String) jObject.get("display");
        description = (String) jObject.get("description");
        incompatible = new ArrayList<String>();
        for (Object item : (JSONArray) jObject.get("incompatible")) {
            incompatible.add((String) item);
        }

        packId = "VanillaTweaks." + name;
        downloaded = false;
        packType = pType;
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
        return downloaded;
    }

    @Override
    public PackType getPackType() {
        return packType;
    }

    @Override
    public void setDownloadedData(ByteArrayInputStream downloadedData) {
        this.downloadedData = downloadedData;

    }
}
