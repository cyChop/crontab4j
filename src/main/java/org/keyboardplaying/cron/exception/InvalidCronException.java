package org.keyboardplaying.cron.exception;

/**
 * An exception to be thrown when the CRON cannot be parsed.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class InvalidCronException extends Exception {

    /** Generated serial version UID. */
    private static final long serialVersionUID = -2849228524072084277L;

    private String cron;

    /**
     * Creates a new instance.
     *
     * @param cron
     *            the invalid CRON expression
     */
    public InvalidCronException(String cron) {
        super();
    }

    /**
     * Returns the CRON expression which caused this exception to be raised.
     *
     * @return the invalid CRON expression
     */
    public String getCron() {
        return cron;
    }
}
