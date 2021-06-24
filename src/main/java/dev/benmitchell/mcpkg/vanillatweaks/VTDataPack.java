package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.DataPack;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTDataPack extends DataPack {
    private class Inner extends VTPack {
        private VTDataPack rPack;

        public Inner(VTDataPack rPack, JSONObject jObject, PackType packType) {
            super(jObject, packType);

            this.rPack = rPack;
        }

        @Override
        public void installTo(Path destination) {
            rPack.installTo(destination);
        }

        @Override
        public void install() {
            rPack.install();
        }
    }

    private Inner inner;

    public VTDataPack(JSONObject jObject) {
        super();

        inner = new Inner(this, jObject, PackType.CRAFTINGPACK);
    }

    @Override
    public String getPackId() {
        return inner.getPackId();
    }

    @Override
    public String getDisplayName() {
        return inner.getDisplayName();
    }

    @Override
    public String getDescription() {
        return inner.getDescription();
    }

    @Override
    public List<String> getDependencies() {
        return inner.getDependencies();
    }

    @Override
    public List<String> getIncompatibilities() {
        return inner.getIncompatibilities();
    }

    @Override
    public boolean isDownloaded() {
        return inner.isDownloaded();
    }

    @Override
    public PackType getPackType() {
        return inner.getPackType();
    }

    @Override
    public void setDownloadedData(ByteArrayInputStream downloadedData) {
        inner.setDownloadedData(downloadedData);
    }
}
