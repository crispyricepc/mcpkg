package dev.benmitchell.mcpkg.exceptions;

public class PackNotFoundException extends MCPKGException {
    public PackNotFoundException(String packId) {
        super("The pack '" + packId + "' could not be found");
    }
}
