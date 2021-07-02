package dev.benmitchell.mcpkg.packs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;

public abstract class Pack {
    public static class Version implements Comparable<Version> {
        public int major;
        public int minor;
        public int revision;

        public Version() {
            this("0.0.0");
        }

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

        public Version(int major, int minor, int revision) {
            this.major = major;
            this.minor = minor;
            this.revision = revision;
        }

        @Override
        public String toString() {
            if (major == 0 && minor == 0 && revision == 0)
                return "";

            StringBuilder builder = new StringBuilder();
            builder.append(major).append(".").append(minor).append(".").append(revision);
            return builder.toString();
        }

        @Override
        public int compareTo(Version o) {
            if (major < o.major)
                return -1;
            if (major > o.major)
                return 1;
            // Majors are the same
            if (minor < o.minor)
                return -1;
            if (minor > o.minor)
                return 1;
            // Minors are the same
            if (revision < o.revision)
                return -1;
            if (revision > o.revision)
                return 1;
            // Versions are the same
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof Version))
                return false;

            return compareTo((Version) o) == 0;
        }
    }

    protected String packId;
    protected String displayName;
    protected String description;
    protected Version version;
    protected List<String> dependencies;
    protected List<String> incompatibilities;
    protected PackType packType;
    protected Optional<File> downloadedData;

    public Pack(String packId, String displayName, String description, Version version, List<String> dependencies,
            List<String> incompatibilities, PackType packType, Optional<File> downloadedData) {
        this.packId = packId;
        this.displayName = displayName;
        this.description = description;
        this.version = version;
        this.dependencies = dependencies;
        this.incompatibilities = incompatibilities;
        this.packType = packType;
        this.downloadedData = downloadedData;
    }

    /**
     * @return The unique ID of the pack
     */
    public String getPackId() {
        return packId;
    }

    /**
     * @return A human readable name for the pack
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return A short description of the pack
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The version of the pack
     */
    public Version getVersion() {
        return version;
    }

    /**
     * @return A list of pack IDs that this pack requires to be installed
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * @return A list of pack IDs that this pack is incompatible with
     */
    public List<String> getIncompatibilities() {
        return incompatibilities;
    }

    /**
     * @return The type (mod, datapack, resource pack etc.) that this package
     *         represents
     */
    public PackType getPackType() {
        return packType;
    }

    /**
     * @return true if the pack data is stored
     */
    public boolean isDownloaded() {
        return downloadedData.isPresent() && downloadedData.get().exists();
    }

    /**
     * @return The data on disk that's been downloaded
     */
    public File getDownloadedData() throws PackNotDownloadedException {
        if (!isDownloaded())
            throw new PackNotDownloadedException(this);
        return downloadedData.get();
    }

    /**
     * Sets the pack to downloaded, with the data being stored in the location at
     * downloadedData
     */
    public void setDownloadedData(File downloadedData) {
        this.downloadedData = Optional.of(downloadedData);
    }

    /**
     * Installs the pack to a given destination
     */
    public void installTo(Path destination) throws IOException, PackNotDownloadedException {
        Path destFile = destination.resolve(toString() + ".zip");
        setDownloadedData(
                Files.move(getDownloadedData().toPath(), destFile, StandardCopyOption.REPLACE_EXISTING).toFile());
    }

    /**
     * Installs the pack to a set destination
     */
    public abstract void install() throws IOException, InvalidDirectoryException, PackNotDownloadedException;

    /**
     * Removes the pack from its installed location
     * 
     * @throws PackNotDownloadedException if the pack hasn't been downloaded
     */
    public void uninstall() throws IOException, PackNotDownloadedException {
        Files.delete(getDownloadedData().toPath());
        downloadedData = Optional.empty();
    }

    @Override
    public String toString() {
        if (getVersion().equals(new Version()))
            return getPackId();
        return getPackId() + "." + getVersion();

    }
}
