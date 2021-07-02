package dev.benmitchell.mcpkg.cli;

import java.util.List;

/**
 * Holds the functions for executing commands. Each function returns an int
 * representing the return status of the program. One function call per command
 */
public class CommandLine {
    /**
     * Installs one or multiple packs
     * 
     * @param packIds The IDs of the packs to install
     * @return
     */
    public static int install(List<String> packIds) {
        // TODO: Not implemented
        return 1;
    }

    /**
     * Uninstalls one or multiple packs
     * 
     * @param packIds The IDs of the packs to uninstall
     */
    public static int uninstall(List<String> packIds) {
        // TODO: Not implemented
        return 1;
    }

    /**
     * Updates one or multiple packs
     * 
     * @param packIds The IDs of the packs to update. Will update all if no IDs are
     *                specified
     */
    public static int update(List<String> packIds) {
        // TODO: Not implemented
        return 1;
    }

    /**
     * Lists all packs
     * 
     * @param installed Whether to limit the listing to only installed packs
     */
    public static int list(boolean installed) {
        // TODO: Not implemented
        return 1;
    }

    /**
     * Searches for packs given a list of keywords
     * 
     * @param keywords  A list of keywords used to identify one or many packs
     * @param installed Whether to limit the search to only installed packs
     */
    public static int search(List<String> keywords, boolean installed) {
        // TODO: Not implemented
        return 1;
    }

    /**
     * Gets detailed information about a pack or packs
     * 
     * @param packIds The IDs of the packs to get information about
     */
    public static int info(List<String> packIds) {
        // TODO: Not implemented
        return 1;
    }
}
