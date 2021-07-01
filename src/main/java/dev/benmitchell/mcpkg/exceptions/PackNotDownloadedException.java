package dev.benmitchell.mcpkg.exceptions;

import dev.benmitchell.mcpkg.packs.Pack;

public class PackNotDownloadedException extends Exception {
    public PackNotDownloadedException(Pack pack) {
        super("The pack '" + pack.getDisplayName() + "' is not downloaded");
    }
}
