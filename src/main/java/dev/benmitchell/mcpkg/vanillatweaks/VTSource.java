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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dev.benmitchell.mcpkg.DownloadManager;
import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.sources.RemoteSource;

public class VTSource extends RemoteSource {
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
            File packCacheFile = new File(Platform.dataPath().toFile(), "vt_" + typeInitials + "categories.json");
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
            JSONParser jParser = new JSONParser();
            JSONObject jObject;
            try (Reader reader = new BufferedReader(new FileReader(packCacheFile))) {
                jObject = (JSONObject) jParser.parse(reader);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }

            // Build the list of packs
            for (Object category : (JSONArray) jObject.get("categories")) {
                for (Object pack : (JSONArray) ((JSONObject) category).get("packs")) {
                    PackType pType = TYPE_INITIAL_MAP.get(typeInitials);
                    switch (pType) {
                        case CRAFTINGTWEAK:
                            packs.add(new VTCraftingPack((JSONObject) pack,
                                    ((JSONObject) category).get("category").toString()));
                            break;
                        case DATAPACK:
                            packs.add(new VTDataPack((JSONObject) pack,
                                    ((JSONObject) category).get("category").toString()));
                            break;
                        case RESOURCEPACK:
                            packs.add(new VTResourcePack((JSONObject) pack,
                                    ((JSONObject) category).get("category").toString()));
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

        for (Pack pack : packs) {
            String typeString = pack.getPackType().toString().toLowerCase();

            // Create request
            VTPack vtPack = (VTPack) pack;
            Map<String, List<String>> jsonMap = new HashMap<String, List<String>>();
            List<String> requestList = new ArrayList<String>(1);
            requestList.add(vtPack.getName());
            jsonMap.put(vtPack.getCategory(), requestList);
            JSONObject jObject = new JSONObject(jsonMap);

            // Create request
            Map<String, String> postMap = new HashMap<String, String>();
            postMap.put("version", "1.17");
            postMap.put("packs", jObject.toJSONString());
            URL requestUrl = new URL("https://vanillatweaks.net/assets/server/zip" + typeString + "s.php");
            String response;
            try {
                response = DownloadManager.postRequest(requestUrl, postMap);
            } catch (URISyntaxException ex) {
                MCPKGLogger.log(Level.ERROR, "Request URL: '" + requestUrl + "' is invalid");
                throw new RuntimeException(ex);
            }
            JSONParser jParser = new JSONParser();
            JSONObject responseJsonObject;
            try {
                responseJsonObject = (JSONObject) jParser.parse(response);
            } catch (ParseException ex) {
                MCPKGLogger.log(Level.ERROR, "A syntax error has occured");
                throw new RuntimeException(ex);
            }

            if (responseJsonObject.get("status").equals("error"))
                throw new VTRemoteException((String) responseJsonObject.get("message"));

            File downloadedFile = tmpDir.resolve(vtPack + ".zip").toFile();

            DownloadManager.downloadToFile(
                    new URL("https://vanillatweaks.net/" + responseJsonObject.get("link").toString()), downloadedFile,
                    false, "Downloading '" + pack + "'...");

            vtPack.setDownloadedData(downloadedFile);
        }
        return packs;
    }
}
