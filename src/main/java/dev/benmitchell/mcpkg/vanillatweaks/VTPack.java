package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTPack implements Pack {
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getIncompatibilities() {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public void installTo(Path destination) {
        // TODO Auto-generated method stub

    }

    @Override
    public void install() {
        // TODO Auto-generated method stub

    }
}
