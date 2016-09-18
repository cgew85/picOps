package io.github.cgew85.picops.model;

import java.util.Random;

public class Session {

    private static Session session = null;
    private static int sessionID;

    private Session() {
        sessionID = createRandomSessionID();
    }

    public static synchronized Session getSession() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }
    private int createRandomSessionID() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        int randNumber = random.nextInt(max - min + 1) + min;

        return randNumber;
    }

    public int getSessionID() {
        return sessionID;
    }

    public int createNewSessionID() {
        this.session = null;
        Session session = new Session();

        return session.getSessionID();
    }
}
