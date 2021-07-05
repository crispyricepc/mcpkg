package dev.benmitchell.mcpkg.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.benmitchell.mcpkg.exceptions.InvalidFlagException;

public class ArgParser {
    private Map<String, Object> flags;
    private List<String> flaglessArgs;

    /**
     * @return True if "--flagName" or "-f"
     */
    private static boolean isAFlag(String arg) {
        return (arg.length() > 2 && arg.substring(0, 2).equals("--") && Character.isAlphabetic(arg.charAt(2)))
                || (arg.length() == 2 && arg.charAt(0) == '-' && Character.isAlphabetic(arg.charAt(1)));
    }

    public ArgParser(Map<String, Object> defaultFlags) {
        flags = defaultFlags;
        flaglessArgs = new ArrayList<String>();
    }

    public Map<String, Object> getEnabledFlags(List<String> arguments) throws InvalidFlagException {
        for (int i = 0; i < arguments.size(); i++) {
            String arg = arguments.get(i);
            if (isAFlag(arg)) {
                int j = 0;
                while (arg.charAt(j) == '-') {
                    j++;
                }
                String key = arg.substring(j, arg.length());
                if (!flags.containsKey(key))
                    throw new InvalidFlagException(key);

                if (flags.get(key) instanceof Boolean)
                    flags.put(key, true);
                else {
                    i++;
                    flags.put(key, arguments.get(i));
                }
            } else
                flaglessArgs.add(arg);
        }
        return flags;
    }

    public List<String> getFlaglessArgs() {
        return flaglessArgs;
    }
}
