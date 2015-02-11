package org.keyboardplaying.cron.model;

/**
 * Represents a set of constraints on a CRON expression (second, minute, hour, day of month/week,
 * month, year).
 * <p/>
 * This objects provides the method {@link #isAllowed(int)}, which determines wether an integer
 * belongs in the authorized values for this segment.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronSegment {

    private int min;
    private int max;
    private int repeat = 1;

    /**
     * Sets the minimal allowed value for this segment.
     *
     * @param min
     *            the minimal allowed value
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Sets the maximal allowed value for this segment.
     *
     * @param max
     *            the maximal allowed value
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Sets the repetition interval for this segment.
     *
     * @param repeat
     *            the repetition interval
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * Tests whether the supplied value is authorized for this segment.
     *
     * @param value
     *            the value to test
     * @return {@code true} if the value is authorized for this segment, {@code false} otherwise
     */
    public boolean isAllowed(int value) {
        return min <= value && value <= max && (min + value) % repeat == 0;
    }
}
