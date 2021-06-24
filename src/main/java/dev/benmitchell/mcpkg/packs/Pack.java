package dev.benmitchell.mcpkg.packs;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;

public interface Pack {
    /**
     * @return The unique ID of the pack
     */
    public String getPackId();

    /**
     * @return A human readable name for the pack
     */
    public String getDisplayName();

    /**
     * @return A short description of the pack
     */
    public String getDescription();

    /**
     * @return A list of pack IDs that this pack requires to be installed
     */
    public List<String> getDependencies();

    /**
     * @return A list of pack IDs that this pack is incompatible with
     */
    public List<String> getIncompatibilities();

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
