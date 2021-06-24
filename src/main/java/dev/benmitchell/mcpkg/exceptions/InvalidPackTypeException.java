package dev.benmitchell.mcpkg.exceptions;

import dev.benmitchell.mcpkg.packs.PackType;

public class InvalidPackTypeException extends RuntimeException {
    public InvalidPackTypeException(PackType packType) {
        super("The pack type " + packType.toString() + " is not valid in this context");
    }
}
