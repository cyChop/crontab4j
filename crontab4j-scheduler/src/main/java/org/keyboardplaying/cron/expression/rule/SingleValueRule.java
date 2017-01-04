package org.keyboardplaying.cron.expression.rule;

/**
 * A representation for fields allowing only one value (e.g. {@code 42}).
 * <p/>
 * Only the value provided at construction will be allowed.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class SingleValueRule extends RangeRule {

    /**
     * Creates a new instance.
     *
     * @param value the single authorized value for this field
     */
    public SingleValueRule(int value) {
        super(value, value);
    }

    /**
     * Returns the single allowed value for this rule.
     *
     * @return the allowed value
     */
    public int getValue() {
        return getMax();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.allows(int)
     */
    @Override
    public boolean allows(int value) {
        return value == getMin();
    }
}
