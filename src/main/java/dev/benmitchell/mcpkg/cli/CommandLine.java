package dev.benmitchell.mcpkg.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import org.jline.terminal.TerminalBuilder;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.exceptions.PackNotFoundException;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.Pack.Version;
import dev.benmitchell.mcpkg.sources.LocalSource;
import dev.benmitchell.mcpkg.sources.PackSource;
import dev.benmitchell.mcpkg.sources.RemoteSource;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource;

/**
 * Holds the functions for executing commands. Each function returns an int representing the return
 * status of the program. One function call per command
 */
public class CommandLine {
    private static int addColourString(StringBuilder builder, String strToColour, Color colour,
            boolean bright, boolean bold) {
        Ansi ansi = Ansi.ansi();
        if (bright)
            ansi = ansi.fgBright(colour);
        else
            ansi = ansi.fg(colour);

        if (bold)
            ansi = ansi.bold();

        String colourString = ansi.a(strToColour).reset().toString();
        builder.append(colourString);
        return strToColour.length();
    }

    private static int addColourString(StringBuilder builder, String strToColor, Color color) {
        return addColourString(builder, strToColor, color, false, false);
    }

    private static String printPackShort(Pack pack, RemoteSource remoteSource, int maxWidth)
            throws IOException {
        // More data is stored in the remote source version, so find that
        try {
            pack = remoteSource.getPack(pack.getPackId());
        } catch (PackNotFoundException ex) {
            // Ignore this error, it's probably not managed by this tool
        }

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
                builder.append(pack.getDescription().substring(0,
                        pack.getDescription().length() - (count - maxWidth) - 3));
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

    private static String printPackLong(Pack pack, RemoteSource remoteSource) throws IOException {
        // More data is stored in the remote source version, so find that
        try {
            pack = remoteSource.getPack(pack.getPackId());
        } catch (PackNotFoundException ex) {
            // Ignore this error, it's probably not managed by this tool
        }

        StringBuilder builder = new StringBuilder();
        addColourString(builder, "id: ", Color.DEFAULT, false, true);
        addColourString(builder, pack.getPackId(), Color.GREEN);
        addColourString(builder, "\n\tname: ", Color.DEFAULT, false, true);
        addColourString(builder, pack.getDisplayName(), Color.BLUE);
        if (!pack.getVersion().equals(new Version())) {
            addColourString(builder, "\n\tversion: ", Color.DEFAULT, false, true);
            addColourString(builder, pack.getVersion().toString(), Color.YELLOW);
        }
        addColourString(builder, "\n\tdescription: ", Color.DEFAULT, false, true);
        builder.append(pack.getDescription());

        return builder.toString();
    }

    private static boolean askForConfirmation(String question) throws IOException {
        System.err.print(question + " [Y/n]: ");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input = reader.readLine();
            if ((input.length() > 0 && Character.toLowerCase(input.charAt(0)) == 'y')
                    || input.length() == 0)
                return true;
        }
        return false;
    }

    /**
     * Local-source-aware install method
     * 
     * @param packs The packs to install
     * @param remoteSource The remote source to get the packs from
     * @param confirm Whether to ask for confirmation when replacing other packs
     * @throws InvalidDirectoryException When a pack that requries a specific directory is
     *         installed, and it's not adhered to
     * @throws PackNotFoundException If localSource.hasPack() fails to catch an outlying case. Treat
     *         as a bug
     * @throws PackNotDownloadedException If a locally installed pack is not downloaded. Treat as a
     *         bug
     */
    private static void installPacks(List<Pack> packs, RemoteSource remoteSource, boolean confirm)
            throws InvalidDirectoryException, IOException, PackNotFoundException,
            PackNotDownloadedException {
        List<Pack> packsToInstall = new ArrayList<Pack>();

        // Check version differences between packs
        LocalSource localSource = new LocalSource();
        for (Pack pack : packs) {
            // If the pack is already installed and its version is >= the version we're
            // trying to install
            if (localSource.hasPack(pack) && localSource.getPack(pack.getPackId()).getVersion()
                    .compareTo(pack.getVersion()) >= 0) {
                if (confirm && !askForConfirmation(pack
                        + " is already installed at the latest version. Do you want to replace?"))
                    // Skip this pack if the user decides to not install
                    continue;
                // Remove the old pack to prepare for the installation of the new one
                localSource.getPack(pack.getPackId()).uninstall();
            }

            packsToInstall.add(pack);
        }

        // Install the new packs
        remoteSource.downloadPacks(packsToInstall);
        for (Pack pack : packsToInstall)
            pack.install();
    }

    /**
     * Prints the help message
     */
    public static int usage() {
        System.out.println("mcpkg - A multi-purpose package manager for Minecraft");
        System.out.println("Commands:");
        System.out.println("    install   <pack-ids>");
        System.out.println("    uninstall <pack-ids>");
        System.out.println("    update    [<pack-ids>]");
        System.out.println("    list      [--installed]");
        System.out.println("    search    <keywords> [--installed]");
        System.out.println("    info      pack-id");
        System.out.println("    --help");

        return 0;
    }

    /**
     * Installs one or multiple packs
     * 
     * @param packIds The IDs of the packs to install
     */
    public static int install(List<String> packIds) throws IOException, PackNotDownloadedException,
            InvalidDirectoryException, PackNotFoundException {
        RemoteSource source = new VTSource();
        List<Pack> packs = source.getPacks(packIds);
        installPacks(packs, source, true);
        return 0;
    }

    /**
     * Uninstalls one or multiple packs
     * 
     * @param packIds The IDs of the packs to uninstall
     */
    public static int uninstall(List<String> packIds)
            throws PackNotDownloadedException, IOException {
        LocalSource source = new LocalSource();
        for (Pack pack : source.getPacks(packIds))
            pack.uninstall();
        return 0;
    }

    /**
     * Updates one or multiple packs
     * 
     * @param packIds The IDs of the packs to update. Will update all if no IDs are specified
     */
    public static int update(List<String> packIds) throws InvalidDirectoryException,
            PackNotFoundException, PackNotDownloadedException, IOException {
        List<Pack> packsToUpdate;
        LocalSource localSource = new LocalSource();
        if (packIds.size() == 0)
            // Update everything
            packsToUpdate = localSource.getPacks();
        else
            packsToUpdate = localSource.getPacks(packIds);

        installPacks(packsToUpdate, new VTSource(), true);

        return 0;
    }

    /**
     * Lists all packs
     * 
     * @param installed Whether to limit the listing to only installed packs
     */
    public static int list(boolean installed) throws IOException, PackNotFoundException {
        PackSource source;
        RemoteSource remoteSource = new VTSource();
        if (installed)
            source = new LocalSource();
        else
            source = remoteSource;

        int consoleWidth = TerminalBuilder.terminal().getWidth();
        for (Pack pack : source.getPacks()) {
            System.out.println(printPackShort(pack, remoteSource, consoleWidth));
        }
        return 0;
    }

    /**
     * Searches for packs given a list of keywords
     * 
     * @param keywords A list of keywords used to identify one or many packs
     * @param installed Whether to limit the search to only installed packs
     */
    public static int search(List<String> keywords, boolean installed)
            throws IOException, PackNotFoundException {
        PackSource source;
        RemoteSource remoteSource = new VTSource();
        if (installed)
            source = new LocalSource();
        else
            source = remoteSource;

        int consoleWidth = TerminalBuilder.terminal().getWidth();
        for (Pack pack : source.searchForPacks(keywords))
            System.out.println(printPackShort(pack, remoteSource, consoleWidth));
        return 0;
    }

    /**
     * Gets detailed information about a pack or packs
     * 
     * @param packIds The IDs of the packs to get information about
     */
    public static int info(List<String> packIds) throws IOException {
        RemoteSource remoteSource = new VTSource();
        for (Pack pack : remoteSource.getPacks(packIds))
            System.out.println(printPackLong(pack, remoteSource));
        return 0;
    }
}
