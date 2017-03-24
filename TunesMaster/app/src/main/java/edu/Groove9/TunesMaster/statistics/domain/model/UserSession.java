package edu.Groove9.TunesMaster.statistics.domain.model;

import java.util.Date;

import edu.Groove9.TunesMaster.logging.Logger;

/**
 * Created by ConnorM on 3/24/2017.
 */

public class UserSession {
    public String userId;

    private static UserSession instance = new UserSession("default");

    private UserSession(String userId) {
        this.userId = userId;
    }

    public static void start(String newUserId) {
        if (!isUserIdValid(newUserId)) {
            throw new RuntimeException("Invalid userId!");
        }
        instance = new UserSession(newUserId);
        Logger.reset(newUserId + ".txt");
    }

    public static void end() {
        instance = null;
    }

    public static UserSession get() {
        return instance;
    }

    public static boolean isUserIdValid(String userId) {
        return userId != null && !userId.equals("");
    }
}
