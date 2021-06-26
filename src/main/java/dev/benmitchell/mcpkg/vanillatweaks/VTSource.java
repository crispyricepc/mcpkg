package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
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
import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidPackTypeException;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.sources.PackSource;

public class VTSource extends PackSource {
    private static final Map<String, PackType> TYPE_INITIAL_MAP;
    static {
        TYPE_INITIAL_MAP = new HashMap<String, PackType>();
        TYPE_INITIAL_MAP.put("rp", PackType.RESOURCEPACK);
        TYPE_INITIAL_MAP.put("dp", PackType.DATAPACK);
        TYPE_INITIAL_MAP.put("ct", PackType.CRAFTINGTWEAK);
    }

    /**
     * Gets metadata about the packs from either a cache file or from the internet
     */
    private List<Pack> getPackCache() {
        List<Pack> packs = new ArrayList<Pack>();

        // Download the pack cache if it doesn't exist, or the date last modified on the
        // file is > 1 day
        for (String typeInitials : new String[] { "rp", "dp", "ct" }) {
            File packCacheFile = new File(Platform.getDataPath(), "vt_" + typeInitials + "categories.json");
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
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }

            // Build the list of packs
            for (Object category : (JSONArray) jObject.get("categories")) {
                for (Object pack : (JSONArray) ((JSONObject) category).get("packs")) {
                    PackType pType = TYPE_INITIAL_MAP.get(typeInitials);
                    switch (pType) {
                        case CRAFTINGTWEAK:
                            packs.add(new VTCraftingPack((JSONObject) pack));
                            break;
                        case DATAPACK:
                            packs.add(new VTDataPack((JSONObject) pack));
                            break;
                        case RESOURCEPACK:
                            packs.add(new VTResourcePack((JSONObject) pack));
                            break;
                        default:
                            throw new InvalidPackTypeException(pType);
                    }
                }
            }
        }

        return packs;
    }

    @Override
    public List<Pack> getPacks(List<String> packIds) {
        List<Pack> packsToReturn = new ArrayList<Pack>();
        for (Pack pack : getPackCache()) {
            if (packIds.contains(pack.getPackId()))
                packsToReturn.add(pack);
        }
        return packsToReturn;
    }

    @Override
    public List<Pack> searchForPacks(List<String> keywords) {
        List<Pack> packsToReturn = new ArrayList<Pack>();

        for (Pack pack : getPackCache()) {
            for (String keyword : keywords) {
                if (pack.getPackId().toLowerCase().contains(keyword.toLowerCase())
                        || pack.getDisplayName().toLowerCase().contains(keyword.toLowerCase())) {
                    packsToReturn.add(pack);
                }
            }
        }

        return packsToReturn;
    }

    @Override
    public List<Pack> downloadPacks(List<Pack> packs) {
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
            try {
                URL requestUrl = new URL("https://vanillatweaks.net/assets/server/zip" + typeString + "s.php");
                String response = DownloadManager.postRequest(requestUrl, postMap);
                JSONParser jParser = new JSONParser();
                JSONObject responseJsonObject = (JSONObject) jParser.parse(response);
                DownloadManager.downloadToFile(
                        new URL("https://vanillatweaks.net/" + responseJsonObject.get("link").toString()),
                        new File("test_dest.zip"), false);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
}
