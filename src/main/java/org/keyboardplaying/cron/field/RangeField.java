package org.keyboardplaying.cron.field;

/**
 * A representation for fields allowing a range (e.g. {@code 42-1337}).
 * <p/>
 * The lower and upper limit and any integer inbetween will be allowed, while all other values will
 * be rejected.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class RangeField implements CronField {

    private int min;
    private int max;

    /**
     * Creates a new instance.
     *
     * @param min
     *            the lower range limit
     * @param max
     *            the upper range limit
     */
    public RangeField(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /*
     * Returns the lower range limit.
     *
     * @return the lower range limit
     */
    protected int getMin() {
        return min;
    }

    /*
     * Returns the upper range limit.
     *
     * @return the upper range limit
     */
    protected int getMax() {
        return max;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.field.CronField.allows(int)
     */
    public boolean allows(int value) {
        return min <= value && value <= max;
    }
}
