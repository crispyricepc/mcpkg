package dev.benmitchell.mcpkg.cli;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import org.jline.terminal.TerminalBuilder;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.MCPKGException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.exceptions.PackNotFoundException;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.Pack.Version;
import dev.benmitchell.mcpkg.sources.LocalSource;
import dev.benmitchell.mcpkg.sources.PackSource;
import dev.benmitchell.mcpkg.sources.RemoteSource;
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
                // MCPKGLogger.err(ex);
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
     */
    public static int install(List<String> packIds) {
        RemoteSource source = new VTSource();
        try {
            List<Pack> packs = source.getPacks(packIds);
            source.downloadPacks(packs);
            for (Pack pack : packs)
                pack.install();
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
            return 1;
        } catch (PackNotDownloadedException ex) {
            MCPKGLogger.err(ex);
            return 1;
        } catch (InvalidDirectoryException ex) {
            MCPKGLogger.err(ex);
            return 1;
        }
        return 0;
    }

    /**
     * Uninstalls one or multiple packs
     * 
     * @param packIds The IDs of the packs to uninstall
     */
    public static int uninstall(List<String> packIds) {
        LocalSource source = new LocalSource();
        try {
            for (Pack pack : source.getPacks(packIds))
                pack.uninstall();
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
            return 1;
        } catch (PackNotDownloadedException ex) {
            MCPKGLogger.err(ex);
            return 1;
        }
        return 0;
    }

    /**
     * Updates one or multiple packs
     * 
     * @param packIds The IDs of the packs to update. Will update all if no IDs are
     *                specified
     */
    public static int update(List<String> packIds) {
        List<Pack> packsToUpdate;
        LocalSource localSource = new LocalSource();
        try {
            if (packIds.size() == 0)
                // Update everything
                packsToUpdate = localSource.getPacks();
            else
                packsToUpdate = localSource.getPacks(packIds);

            // Check version differences between packs
            RemoteSource remoteSource = new VTSource();
            List<Pack> packsToInstall = new ArrayList<Pack>();
            List<Pack> packsToUninstall = new ArrayList<Pack>();
            for (Pack pack : packsToUpdate) {
                try {
                    Pack remotePack = remoteSource.getPack(pack.getPackId());
                    if (pack.getVersion().compareTo(remotePack.getVersion()) < 0) {
                        packsToInstall.add(remotePack);
                        packsToUninstall.add(pack);
                    }
                } catch (PackNotFoundException ex) {
                    MCPKGLogger.log(Level.WARNING, "Couldn't update " + pack + ". " + ex.getMessage());
                }
            }

            // Download and install the remaning packs
            remoteSource.downloadPacks(packsToInstall);
            for (Pack packToInstall : packsToInstall) {
                try {
                    packToInstall.install();
                } catch (MCPKGException ex) {
                    MCPKGLogger.err(ex);
                }
            }
            for (Pack packToUninstall : packsToUninstall) {
                try {
                    packToUninstall.uninstall();
                } catch (MCPKGException ex) {
                    MCPKGLogger.err(ex);
                }
            }

            if (packsToInstall.size() == 0)
                MCPKGLogger.log(Level.INFO, "No packs require update");
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
            return 1;
        }
        return 0;
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
        PackSource source;
        if (installed)
            source = new LocalSource();
        else
            source = new VTSource();

        try {
            int consoleWidth = TerminalBuilder.terminal().getWidth();
            for (Pack pack : source.searchForPacks(keywords))
                System.out.println(printPackShort(pack, consoleWidth));
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
        }
        return 0;
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
