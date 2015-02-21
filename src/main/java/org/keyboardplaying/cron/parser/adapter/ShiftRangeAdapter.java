package org.keyboardplaying.cron.parser.adapter;

import java.util.ArrayList;
import java.util.List;

import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Common implementation of {@link RangeAdapter} to shift the range rules in order to match the
 * default values of crontab4j implementation.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public abstract class ShiftRangeAdapter implements RangeAdapter {

    private final int adaptedMin;
    private final int adaptedMax;
    private final int shift;

    /**
     * Creates a new instance.
     *
     * @param adaptedMin
     *            the minimal allowed value for the shifted range
     * @param adaptedMax
     *            the maximal allowed value for the shifted range
     * @param ref
     *            a reference value for the range to be shifted (e.g. integer representation of a
     *            specific day)
     * @param adaptedRef
     *            the equivalent reference value in the shifted referential
     */
    protected ShiftRangeAdapter(int adaptedMin, int adaptedMax, int ref, int adaptedRef) {
        this.adaptedMin = adaptedMin;
        this.adaptedMax = adaptedMax;
        this.shift = adaptedRef - ref;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.SingleValueRule)
     */
    @Override
    public CronRule adapt(SingleValueRule rule) {
        return new SingleValueRule(shift(rule.getValue()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.RangeRule)
     */
    @Override
    public CronRule adapt(RangeRule rule) {
        int min = shift(rule.getMin());
        int max = shift(rule.getMax());

        CronRule result;
        if (min <= max) {
            result = new RangeRule(min, max);
        } else {
            result = new MultipleRule(new RangeRule(adaptedMin, max),
                    new RangeRule(min, adaptedMax));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.RepeatRule)
     */
    @Override
    public CronRule adapt(RepeatRule rule) {
        int min = shift(rule.getMin());
        int max = shift(rule.getMax());

        CronRule result;
        if (min <= max) {
            result = new RepeatRule(min, max, rule.getStep());
        } else {
            // XXX min/max calculation is tricky, but two repeats should work
            List<CronRule> allowed = new ArrayList<>();
            for (int i = rule.getMin(); i <= rule.getMax(); i++) {
                if (rule.allows(i)) {
                    allowed.add(new SingleValueRule(shift(i)));
                }
            }
            result = new MultipleRule(allowed);
        }

        return result;
    }

    /**
     * Shifts the value according to the adapter's settings.
     *
     * @param value
     *            the value to shift
     * @return the shifted value
     */
    private int shift(int value) {
        int shifted = (value + shift) % adaptedMax;
        // in case of negative shift
        return shifted < adaptedMin ? shifted + adaptedMax : shifted;
    }
}
