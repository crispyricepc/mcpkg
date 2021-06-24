package dev.benmitchell.mcpkg.exceptions;

import dev.benmitchell.mcpkg.packs.Pack;

public class MissingDependencyException extends Exception {
    public MissingDependencyException(String dependencyId, Pack pack) {
        super("The pack '" + dependencyId + "' is required to install '" + pack.getDisplayName() + "'");
    }
}