package org.keyboardplaying.cron.field;

/**
 * Represents a set of constraints on a CRON expression (second, minute, hour, day of month/week,
 * month, year).
 * <p/>
 * This object provides the method {@link #allows(int)}, which determines wether an integer
 * belongs in the authorized values for this segment.
 * <p/>
 * It is recommended the implementations of this class would be immutables.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public interface CronField {

    /**
     * Tests whether the supplied value is authorized for this segment.
     *
     * @param value
     *            the value to test
     * @return {@code true} if the value is authorized for this segment, {@code false} otherwise
     */
    boolean allows(int value);
}
