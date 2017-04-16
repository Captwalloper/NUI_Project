package edu.Groove9.TunesMaster.logging;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ConnorM on 3/24/2017.
 */

public class Logger {
    private String logfile;

    private static Logger instance = new Logger("default.txt");

    private Logger(String logfile) {
        setupFile(logfile);
        this.logfile = logfile;
    }

    public static void reset(String logfile) {
        instance = new Logger(logfile);
    }

    public static Logger get() {
        return instance;
    }

    private void setupFile(String filename) {
        File LogFile = new File("sdcard/"+filename);
        try {
            FileWriter LogWriter = new FileWriter(LogFile, true);
            BufferedWriter out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create log file:\n" + e.getMessage());
        }
    }

    public void log(UserEvent userEvent) {
        log(userEvent.toString());
    }

    private void log(String message) {
        try {
            File LogFile = new File("sdcard/"+logfile);
            FileWriter LogWriter = new FileWriter(LogFile, true);
            BufferedWriter out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write(message + ">" + date + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
