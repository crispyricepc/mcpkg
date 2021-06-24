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
     * @return The type (mod, datapack, resource pack etc.) that this package
     *         represents
     */
    public PackType getPackType();

    /**
     * Sets the pack to downloaded, with the data being the contents of the byte
     * array
     */
    public void setDownloadedData(ByteArrayInputStream downloadedData);

    /**
     * Installs the pack to a given destination
     * 
     * @throws MissingDependencyException if not all the required dependencies are
     *                                    installed
     */
    public void installTo(Path destination);

    /**
     * Installs the pack to a given destination
     * 
     * @throws MissingDependencyException if not all the required dependencies are
     *                                    installed
     * 
     * @see #installTo(Path)
     */
    public void install();
}
