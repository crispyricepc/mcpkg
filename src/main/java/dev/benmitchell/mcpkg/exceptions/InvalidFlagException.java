package dev.benmitchell.mcpkg.exceptions;

public class InvalidFlagException extends MCPKGException {
    public InvalidFlagException(String flagName) {
        super("The flag --" + flagName + " is not a valid flag");
    }
}
