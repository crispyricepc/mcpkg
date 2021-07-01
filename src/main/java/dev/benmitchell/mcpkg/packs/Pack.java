package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;

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
     * @return The data on disk that's been downloaded
     */
    public File getDownloadedData();

    /**
     * Sets the pack to downloaded, with the data being stored in the location at
     * downloadedData
     */
    public void setDownloadedData(File downloadedData);

    /**
     * Installs the pack to a given destination
     * 
     * @throws MissingDependencyException if not all the required dependencies are
     *                                    installed
     */
    public void installTo(Path destination) throws IOException, PackNotDownloadedException;

    /**
     * Installs the pack to a set destination
     * 
     * @throws MissingDependencyException if not all the required dependencies are
     *                                    installed
     */
    public void install() throws IOException, InvalidDirectoryException, PackNotDownloadedException;
}
