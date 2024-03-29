package dev.benmitchell.mcpkg.exceptions;

import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public class MissingDependencyException extends MCPKGException {
    public MissingDependencyException(List<String> dependencyIds, Pack pack) {
        super("The packs " + String.join(", ", dependencyIds) + "are required to install "
                + pack.getPackId());
    }
}
