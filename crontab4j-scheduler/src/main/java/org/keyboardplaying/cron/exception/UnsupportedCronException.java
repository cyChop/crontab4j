package org.keyboardplaying.cron.exception;

/**
 * An exception to be thrown when the CRON cannot be parsed.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class UnsupportedCronException extends RuntimeException {

    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = -2797402955844036857L;

    private final String cron;
    private final boolean valid;

    /**
     * Creates a new instance.
     *
     * @param cron  the invalid CRON expression
     * @param valid {@code true} if the CRON expression is valid
     */
    public UnsupportedCronException(String cron, boolean valid) {
        super();
        this.cron = cron;
        this.valid = valid;
    }

    /**
     * Returns the CRON expression which caused this exception to be raised.
     *
     * @return the invalid CRON expression
     */
    public String getCron() {
        return cron;
    }

    /**
     * Returns {@code true} if the CRON is valid according to the parser, {@code false} otherwise.
     * <p/>
     * A valid CRON may not be supported because of the complexity of implementing its triggering in the scheduler.
     *
     * @return {@code true} if the CRON is valid, {@code false} otherwise
     */
    public boolean isValid() {
        return valid;
    }
}
