package dev.benmitchell.mcpkg.cli;

import java.io.IOException;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import org.jline.terminal.TerminalBuilder;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.Pack.Version;
import dev.benmitchell.mcpkg.sources.LocalSource;
import dev.benmitchell.mcpkg.sources.PackSource;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource;

/**
 * Holds the functions for executing commands. Each function returns an int
 * representing the return status of the program. One function call per command
 */
public class CommandLine {
    private static int addColourString(StringBuilder builder, String strToColour, Color colour) {
        String colourString = Ansi.ansi().fg(colour).a(strToColour).reset().toString();
        builder.append(colourString);
        return strToColour.length();
    }

    private static String printPackShort(Pack pack, int maxWidth) {
        int count = 0;
        StringBuilder builder = new StringBuilder();
        count += addColourString(builder, pack.getDisplayName(), Color.BLUE);
        builder.append(" (");
        count += " (".length();
        count += addColourString(builder, pack.getPackId(), Color.GREEN);

        if (!pack.getVersion().equals(new Version())) {
            builder.append(" v.");
            count += " v.".length();
            count += addColourString(builder, pack.getVersion().toString(), Color.YELLOW);
        }
        builder.append(") ");
        count += ") ".length();

        count += pack.getDescription().length();

        if (count >= maxWidth) {
            try {
                builder.append(
                        pack.getDescription().substring(0, pack.getDescription().length() - (count - maxWidth) - 3));
                builder.append("...");
            } catch (StringIndexOutOfBoundsException ex) {
                // Do nothing, we just want to catch the exception
                MCPKGLogger.err(ex);
            }
        } else {
            builder.append(pack.getDescription());
        }
        return builder.toString();
    }

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
        PackSource source;
        if (installed)
            source = new LocalSource();
        else
            source = new VTSource();

        try {
            int consoleWidth = TerminalBuilder.terminal().getWidth();
            for (Pack pack : source.getPacks()) {
                System.out.println(printPackShort(pack, consoleWidth));
            }
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
        }
        return 0;
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
