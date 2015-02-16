package org.keyboardplaying.cron.expression.rule;

/**
 * A representation for fields allowing a range (e.g. {@code 42-1337}).
 * <p/>
 * The lower and upper limit and any integer inbetween will be allowed, while all other values will
 * be rejected.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class RangeRule implements CronRule {

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
    public RangeRule(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Supplied minimum " + min
                    + " is higher than maximum " + max);
        }
        this.min = min;
        this.max = max;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.hasMax()
     */
    @Override
    public boolean hasMax() {
        return true;
    }

    /**
     * Returns the lower range limit.
     *
     * @return the lower range limit
     */
    public int getMin() {
        return min;
    }

    /**
     * Returns the upper range limit.
     *
     * @return the upper range limit
     */
    @Override
    public int getMax() {
        return max;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.allows(int)
     */
    @Override
    public boolean allows(int value) {
        return min <= value && value <= max;
    }
}
