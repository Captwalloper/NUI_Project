package edu.Groove9.TunesMaster.logging;

/**
 * Created by ConnorM on 3/24/2017.
 */

public class UserEvent {
    private Source source;
    private Action action;

    public UserEvent(Source source, Action action) {
        this.source = source;
        this.action = action;
    }

    @Override
    public String toString() {
        return source.name() + "|" + action.name();
    }

    public enum Source {
        Voice,
        Gesture,
        Touch
    }

    public enum Action {
        PlayPause,
        Next,
        Last,
        Shuffle
    }
}
