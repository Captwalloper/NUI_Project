package edu.Groove9.TunesMaster.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ConnorM on 3/23/2017.
 */

public class VoiceRecognizer implements IVoiceRecognizer {

    @Override
    public Runnable determineAction(String input, Map<String, Runnable> commands) throws CommandParseException {
        Set<String> commandNames = commands.keySet();
        for (String commandName : commandNames) {
            List<String> aliases = extractAliases(commandName);
            for (String alias : aliases) {
                if (positiveMatch(input, alias)) {
                    return commands.get(commandName);
                }
            }
        }
        throw new CommandParseException(input, new ArrayList(commands.keySet()));
    }

    @Override
    public String determineCommandName(String input, Map<String, Runnable> commands) throws CommandParseException {
        Set<String> commandNames = commands.keySet();
        for (String commandName : commandNames) {
            List<String> aliases = extractAliases(commandName);
            for (String alias : aliases) {
                if (positiveMatch(input, alias)) {
                    return aliases.get(0);
                }
            }
        }
        throw new CommandParseException(input, new ArrayList(commands.keySet()));
    }

    private List<String> extractAliases(String commandName) {
        List<String> aliases = new ArrayList<java.lang.String>();
        if (commandName.contains("|")) {
            aliases = Arrays.asList(commandName.split("\\|"));
        }
        else {
            aliases.add(commandName);
        }
        return aliases;
    }

    private boolean positiveMatch(String input, String alias) {
        String standardizedInput = standardize(input);
        String standardizedAlias = standardize(alias);
        return standardizedInput.contains(standardizedAlias);
    }

    private String standardize(String input) {
        return input.toLowerCase();
    }

}
