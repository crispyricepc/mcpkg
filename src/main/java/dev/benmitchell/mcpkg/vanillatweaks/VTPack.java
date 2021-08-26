package dev.benmitchell.mcpkg.vanillatweaks;

import java.util.ArrayList;
import java.util.Optional;

import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource.VTJson.Category.RemotePack;

public abstract class VTPack extends Pack {
    // Vanilla tweaks remote data
    private String name;
    private String category;

    public VTPack(RemotePack jsonPack, PackType pType, String category) {
        super("VanillaTweaks." + jsonPack.name, jsonPack.display, jsonPack.description, new Version(jsonPack.version),
                new ArrayList<String>(), new ArrayList<String>(), pType, Optional.empty());
        name = jsonPack.name;
        for (var item : jsonPack.incompatible) {
            incompatible.add(item);
        }

        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
