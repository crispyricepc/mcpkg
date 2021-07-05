package dev.benmitchell.mcpkg;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.benmitchell.mcpkg.cli.ArgParser;
import dev.benmitchell.mcpkg.cli.CommandLine;
import dev.benmitchell.mcpkg.exceptions.MCPKGException;

public class Main {
    public static void main(String[] args) {
        ArgParser globalFlagsParser = new ArgParser(new HashMap<String, Object>() {
            {
                put("y", false);
                put("installed", false);
                put("minecraft-dir", Platform.DOT_MINECRAFT_PATH.toString());
            }
        });

        Map<String, Object> enabledFlags;
        try {
            enabledFlags = globalFlagsParser.getEnabledFlags(Arrays.asList(args));

            List<String> subcommands = globalFlagsParser.getFlaglessArgs();
            if (subcommands.get(0).equals("install"))
                System.exit(CommandLine.install(subcommands.subList(1, subcommands.size())));
            if (subcommands.get(0).equals("uninstall"))
                System.exit(CommandLine.uninstall(subcommands.subList(1, subcommands.size())));
            if (subcommands.get(0).equals("update"))
                System.exit(CommandLine.update(subcommands.subList(1, subcommands.size())));
            if (subcommands.get(0).equals("list"))
                System.exit(CommandLine.list((Boolean) enabledFlags.get("installed")));
            if (subcommands.get(0).equals("search"))
                System.exit(CommandLine.search(subcommands.subList(1, subcommands.size()),
                        (Boolean) enabledFlags.get("installed")));
            if (subcommands.get(0).equals("info"))
                System.exit(CommandLine.info(subcommands.subList(1, subcommands.size())));

        } catch (IOException ex) {
            MCPKGLogger.err(ex);
            System.exit(1);
        } catch (MCPKGException ex) {
            MCPKGLogger.err(ex);
            System.exit(1);
        }
    }
}
