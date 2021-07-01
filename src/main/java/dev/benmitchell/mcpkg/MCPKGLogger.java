package dev.benmitchell.mcpkg;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi.Color;

public class MCPKGLogger {
    private static final Map<Level, String> LOG_LEVEL_STRINGS;
    static {
        AnsiConsole.systemInstall();
        LOG_LEVEL_STRINGS = new HashMap<Level, String>();
        LOG_LEVEL_STRINGS.put(Level.ALL, "");
        LOG_LEVEL_STRINGS.put(Level.TRACE, "[" + Ansi.ansi().fg(Color.BLUE).a("TRACE").reset() + "]");
        LOG_LEVEL_STRINGS.put(Level.DEBUG, "[" + Ansi.ansi().fg(Color.BLUE).a("DEBUG").reset() + "]");
        LOG_LEVEL_STRINGS.put(Level.INFO, "[" + Ansi.ansi().fg(Color.GREEN).a("INFO").reset() + "]");
        LOG_LEVEL_STRINGS.put(Level.WARNING, "[" + Ansi.ansi().fg(Color.YELLOW).a("WARNING").reset() + "]");
        LOG_LEVEL_STRINGS.put(Level.ERROR, "[" + Ansi.ansi().fg(Color.RED).a("ERROR").reset() + "]");
        LOG_LEVEL_STRINGS.put(Level.OFF, "");
        AnsiConsole.systemUninstall();
    };

    private static boolean isLoggable(Level level) {
        if (System.getenv("MCPKG_DEBUG") != null)
            return true;

        return (level.getSeverity() > Level.DEBUG.getSeverity());
    }

    public static void err(Throwable thrown) {
        log(Level.ERROR, thrown.getMessage());
    }

    public static void log(Level level, String msg, Object... params) {
        if (!isLoggable(level))
            return;

        System.err.printf("{0} ", LOG_LEVEL_STRINGS.get(level));
        System.err.printf(msg, params);
        System.err.println();
    }
}
