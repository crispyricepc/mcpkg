package dev.benmitchell.mcpkg.exceptions;

import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;

public class IncompatiblePackException extends MCPKGException {
    public IncompatiblePackException(List<String> packIds, Pack pack) {
        super("The packs " + String.join(", ", packIds) + "are incompatible with "
                + pack.getPackId());
    }
}
