package dev.benmitchell.mcpkg.exceptions;

import java.nio.file.Path;

public class InvalidDirectoryException extends Exception {
    public InvalidDirectoryException(Path directory, String reason) {
        super("The directory '" + directory.toString() + "' is not valid. Reason: " + reason);
    }
}
