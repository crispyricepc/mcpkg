package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.benmitchell.mcpkg.DownloadManager;
import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.sources.RemoteSource;

public class VTSource extends RemoteSource {
    public static class VTJson {
        public static class Category {
            public static class Warning {
                public String text;
                public String color;
            }

            public static class RemotePack {
                public String name;
                public String display;
                public String previewExtension;
                public String description;
                public List<String> incompatible;
                public List<String> requires;
                public String version;
                public String video;
                public boolean experiment;
                public int lastupdated;
            }

            public String category;
            public List<RemotePack> packs;
            public Warning warning;
        }

        public List<Category> categories;
    }

    private List<Pack> packs = null;

    public class VTRemoteException extends RuntimeException {
        public VTRemoteException(String errorMessage) {
            super("vanillatweaks.net returned the following error: " + errorMessage);
        }
    }

    private static final Map<String, PackType> TYPE_INITIAL_MAP;
    static {
        TYPE_INITIAL_MAP = new HashMap<String, PackType>();
        TYPE_INITIAL_MAP.put("rp", PackType.RESOURCEPACK);
        TYPE_INITIAL_MAP.put("dp", PackType.DATAPACK);
        TYPE_INITIAL_MAP.put("ct", PackType.CRAFTINGTWEAK);
    }

    @Override
    public List<Pack> getPacks() throws IOException {
        if (packs != null) {
            return packs;
        }
        packs = new ArrayList<Pack>();

        // Download the pack cache if it doesn't exist, or the date last modified on the
        // file is > 1 day
        for (String typeInitials : new String[] { "rp", "dp", "ct" }) {
            File packCacheFile = new File(Platform.config.dataPath.toFile(), "vt_" + typeInitials + "categories.json");
            if (!packCacheFile.exists()
                    || Calendar.getInstance().getTimeInMillis() - packCacheFile.lastModified() > 1000 * 24 * 60 * 60) {
                try {
                    DownloadManager.downloadToFile(new URL(
                            "https://vanillatweaks.net/assets/resources/json/1.17/" + typeInitials + "categories.json"),
                            packCacheFile, false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            // JSON convert the file
            ObjectMapper mapper = new ObjectMapper();
            VTJson jsonData;
            try (Reader reader = new BufferedReader(new FileReader(packCacheFile))) {
                jsonData = mapper.readValue(reader, VTJson.class);
            }

            // Build the list of packs
            for (var category : jsonData.categories) {
                for (var pack : category.packs) {
                    PackType pType = TYPE_INITIAL_MAP.get(typeInitials);
                    switch (pType) {
                        case CRAFTINGTWEAK:
                            packs.add(new VTCraftingPack(pack, category.category));
                            break;
                        case DATAPACK:
                            packs.add(new VTDataPack(pack, category.category));
                            break;
                        case RESOURCEPACK:
                            packs.add(new VTResourcePack(pack, category.category));
                            break;
                        default:
                            throw new RuntimeException("This packtype (" + pType.toString() + ") is not valid");
                    }
                }
            }
        }

        return packs;
    }

    @Override
    public List<Pack> downloadPacks(List<Pack> packs) throws IOException {
        Path tmpDir = Files.createTempDirectory("mcpkg");

        ObjectMapper mapper = new ObjectMapper();

        for (Pack pack : packs) {
            String typeString = pack.getPackType().toString().toLowerCase();

            // Create list of packs for the request
            VTPack vtPack = (VTPack) pack;
            Map<String, List<String>> packListMap = new HashMap<String, List<String>>();
            List<String> requestList = new ArrayList<String>(1);
            requestList.add(vtPack.getName());
            packListMap.put(vtPack.getCategory(), requestList);
            String packListJson = mapper.writeValueAsString(packListMap);

            // Create request
            Map<String, String> postMap = new HashMap<String, String>();
            postMap.put("version", "1.17");
            postMap.put("packs", packListJson);

            // Post request
            URL requestUrl = new URL("https://vanillatweaks.net/assets/server/zip" + typeString + "s.php");
            String response;
            try {
                response = DownloadManager.postRequest(requestUrl, postMap);
            } catch (URISyntaxException ex) {
                MCPKGLogger.log(Level.ERROR, "Request URL: '" + requestUrl + "' is invalid");
                throw new RuntimeException(ex);
            }
            JsonNode responseNode = mapper.readTree(response);

            if (responseNode.get("status").asText().equals("error"))
                throw new VTRemoteException(responseNode.get("message").asText("No error message"));

            File downloadedFile = tmpDir.resolve(vtPack + ".zip").toFile();

            DownloadManager.downloadToFile(new URL("https://vanillatweaks.net/" + responseNode.get("link").asText()),
                    downloadedFile, false, "Downloading '" + pack + "'...");

            vtPack.setDownloadedData(downloadedFile);
        }
        return packs;
    }
}
