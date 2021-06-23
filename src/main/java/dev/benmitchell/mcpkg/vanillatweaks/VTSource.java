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
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.sources.PackSource;

public class VTSource extends PackSource {
    private static final Map<String, PackType> TYPE_INITIAL_MAP;
    static {
        TYPE_INITIAL_MAP = new HashMap<String, PackType>();
        TYPE_INITIAL_MAP.put("rp", PackType.RESOURCEPACK);
        TYPE_INITIAL_MAP.put("dp", PackType.DATAPACK);
        TYPE_INITIAL_MAP.put("ct", PackType.CRAFTINGPACK);
    }

    private List<VTPack> getPackCache() {
        List<VTPack> packs = new ArrayList<VTPack>();

        // Download the pack cache if it doesn't exist, or the date last modified on the
        // file is > 1 day
        for (String typeInitials : new String[] { "rp", "dp", "ct" }) {
            File packCacheFile = new File(Platform.getDataPath(), "vt_" + typeInitials + "categories.json");
            if (!packCacheFile.exists()
                    || Calendar.getInstance().getTimeInMillis() - packCacheFile.lastModified() > 1000 * 24 * 60 * 60) {
                try {
                    DownloadManager.downloadToFile(new URL(
                            "https://vanillatweaks.net/assets/resources/json/1.17/" + typeInitials + "categories.json"),
                            packCacheFile, true);
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
                    packs.add(new VTPack((JSONObject) pack, TYPE_INITIAL_MAP.get(typeInitials)));
                }
            }
        }

        return packs;
    }

    @Override
    public List<Pack> getPacks(List<String> packIds) {
        List<Pack> packsToReturn = new ArrayList<Pack>();
        for (VTPack pack : getPackCache()) {
            if (packIds.contains(pack.getPackId()))
                packsToReturn.add(pack);
        }
        return packsToReturn;
    }

    @Override
    public List<Pack> searchForPacks(List<String> keywords) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Pack> downloadPacks(List<Pack> packs) {
        // TODO Auto-generated method stub
        return null;
    }
}
