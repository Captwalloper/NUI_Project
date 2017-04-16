package edu.Groove9.TunesMaster.voice;

import java.util.Map;

/**
 * Created by ConnorM on 3/23/2017.
 */

public interface IVoiceRecognizer {
    public Runnable determineAction(String input, Map<String, Runnable> commands) throws CommandParseException;
    public String determineCommandName(String input, Map<String, Runnable> commands) throws CommandParseException;
}
