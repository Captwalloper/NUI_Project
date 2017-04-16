package edu.Groove9.TunesMaster.voice;

import java.io.*;
import java.util.List;

/**
 * Created by comcc_000 on 4/8/2017.
 */

public class CommandParseException extends Exception {

    private String input;
    private List<String> commands;

    public CommandParseException(String input, List<String> commands) {
        this.input = input;
        this.commands = commands;
    }

    @Override
    public String getMessage(){
        String display = "";
        final String sep = "\n\t";
        display += "Input:";
        display += sep + input;
        display += "\n" + "Commands:";
        for (String command : commands) {
            display += sep + command;
        }
        return display;
    }
}
