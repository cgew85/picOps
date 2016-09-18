package io.github.cgew85.picops.model;

import java.util.Random;

/**
 * The Class Session.
 */
public class Session {

    /**
     * The session.
     */
    private static Session session = null;

    /**
     * The session id.
     */
    private static int sessionID;

    /**
     * Instantiates a new session.
     */
    private Session() {
        sessionID = createRandomSessionID();
    }

    /**
     * Gets the session. (Singleton Pattern)
     *
     * @return the session
     */
    public static synchronized Session getSession() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    /**
     * Creates the random session id.
     *
     * @return the int
     */
    private int createRandomSessionID() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        int randNumber = random.nextInt(max - min + 1) + min;

        return randNumber;
    }

    /**
     * Gets the session id.
     *
     * @return the session id
     */
    public int getSessionID() {
        return sessionID;
    }

    public int createNewSessionID() {
        this.session = null;
        Session session = new Session();

        return session.getSessionID();
    }
}
