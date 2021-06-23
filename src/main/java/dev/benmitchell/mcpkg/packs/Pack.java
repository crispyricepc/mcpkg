package dev.benmitchell.mcpkg.packs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

public interface Pack {
    /**
     * Returns the unique ID of the pack
     * 
     * @return
     */
    public String getPackId();

    /**
     * @return true if the pack data is stored
     */
    public boolean isDownloaded();

    /**
     * Sets the pack to downloaded, with the data being the contents of the byte
     * array
     */
    public void setDownloadedData(ByteArrayInputStream downloadedData);

    /**
     * Installs the pack to a given destination
     */
    public void installTo(Path destination);

    /**
     * Installs the pack to a given destination
     * 
     * @see #installTo(Path)
     */
    public void install();
}
