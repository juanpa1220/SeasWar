package client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Parser {
    private Map<String, Consumer<String[]>> commands;
    private final String ARG_SEPARATOR = " \\.<>";
    private final String ARG_PATTERN = "<[^" + ARG_SEPARATOR + "]+>";
    private final String TARG_PATTERN = "<[^" + ARG_SEPARATOR + "]+\\.\\.\\.>";

    public Parser() {
        commands = new HashMap<String, Consumer<String[]>>();
    }

    /**
     * registers a new a new command if the pattern is not already registered
     * @param cmdPattern command pattern string (arguments have to be surrounded with "&lt;&gt;")
     * @param func function which takes an array of the given arguments
     */
    public void registerCommand(String cmdPattern, Consumer<String[]> func) {
        if(commands.containsKey(cmdPattern)) {
            commands.remove(cmdPattern);
        }
        commands.put(cmdPattern, func);
    }

    /**
     * parses the given input and if it could be matched to a registered command it calls the according function
     * @param input the raw input which should be parsed
     */
    public void parse(String input) {
        input = input.replaceAll(" {2,}", " ");
        for(String pattern : commands.keySet()) {
            if(input.matches("^" + toRegEx(pattern) + "$")) {
                commands.get(pattern).accept(getArgs(input, pattern));
                return;
            }
        }
        String[] suggestions = suggestions(input);
        if(suggestions.length > 0) {
            String usage = "Usage:\n";
            for(int sug = 0; sug < suggestions.length; sug++) {
                usage += String.format("  %s%s", suggestions[sug], sug < suggestions.length - 1 ? "\n" : "");
            }
            throw new IllegalArgumentException(usage);
        } else {
            throw new IllegalArgumentException("Unknown Command!");
        }
    }

    /**
     * @param input for which possible matches are searched
     * @return a list of possible patterns for the given input
     */
    public String[] suggestions(String input) {
        List<String> result = new ArrayList<>();
        for(String pattern : commands.keySet()) {
            String[] patternParts = pattern.split(" ");
            for(int part = 0; part < patternParts.length; part++) {
                if(input.matches("^" + toRegEx(String.join(" ", Arrays.copyOfRange(patternParts, 0, patternParts.length-part))))) {
                    result.add(pattern);
                    break;
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @return an array containing all registered command patterns
     */
    public String[] getRegisteredCommands() {
        return commands.keySet().toArray(new String[commands.size()]);
    }

    private String toRegEx(String cmdPatten) {
        return cmdPatten.replaceAll(ARG_PATTERN, "[^ ]+").replaceAll(TARG_PATTERN, ".+");
    }

    private String[] getArgs(String input, String pattern) {
        List<String> result = new ArrayList<String>();
        String[] inputParts = input.split(" ");
        String[] patternParts = pattern.split(" ");
        for(int part = 0; part < patternParts.length; part++) {
            if(patternParts[part].matches("^" + ARG_PATTERN + "$")) {
                result.add(inputParts[part]);
            } else if (patternParts[part].matches("^" + TARG_PATTERN + "$")) {
                result.add(String.join(" ", Arrays.copyOfRange(inputParts, part, inputParts.length)));
            }
        }
        return result.toArray(new String[result.size()]);
    }

}