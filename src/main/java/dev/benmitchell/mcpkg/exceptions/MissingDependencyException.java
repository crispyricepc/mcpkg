package dev.benmitchell.mcpkg.exceptions;

import dev.benmitchell.mcpkg.packs.Pack;

public class MissingDependencyException extends Exception {
    public MissingDependencyException(List<String> dependencyIds, Pack pack) {
        super("The packs " + String.join(", ", dependencyIds) + "are required to install " + pack.getPackId());
    }
}