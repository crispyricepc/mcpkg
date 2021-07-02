package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;

public interface Pack {
    public class Version {
        public int major;
        public int minor;
        public int revision;

        public Version(String str) {
            if (str == null) {
                major = 0;
                minor = 0;
                revision = 0;
                return;
            }

            String[] versions = str.split("\\.");
            major = Integer.parseInt(versions[0]);
            minor = Integer.parseInt(versions[1]);
            revision = Integer.parseInt(versions[2]);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(major).append(".").append(minor).append(".").append(revision);
            return builder.toString();
        }
    }

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
     * @return The version of the pack
     */
    public Version getVersion();

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

    /**
     * Removes the pack from its installed location
     * 
     * @throws PackNotDownloadedException if the pack hasn't been downloaded
     */
    public void uninstall() throws IOException, PackNotDownloadedException;
}
