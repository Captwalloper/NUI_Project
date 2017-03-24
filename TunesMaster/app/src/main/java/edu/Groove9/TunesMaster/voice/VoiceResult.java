package edu.Groove9.TunesMaster.voice;

/**
 * Created by ConnorM on 3/23/2017.
 */

public class VoiceResult {
    private boolean success;
    private String value;

    public VoiceResult(boolean success, String value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getValue() {
        return value;
    }
}
