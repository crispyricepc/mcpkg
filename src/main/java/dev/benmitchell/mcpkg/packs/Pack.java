package dev.benmitchell.mcpkg.packs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

public interface Pack {
    public String getPackId();

    public boolean isDownloaded();

    public ByteArrayInputStream getDownloadedData();

    public void setDownloadedData(ByteArrayInputStream downloadedData);

    public void installTo(Path destination);

    public void install();
}
