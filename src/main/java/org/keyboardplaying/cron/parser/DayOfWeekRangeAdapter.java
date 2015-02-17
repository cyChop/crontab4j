package org.keyboardplaying.cron.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * A utility for adapting the day range for the computer.
 * <p/>
 * The number is not identical depending on the syntax (Unix uses 0 or 7 as Sunday, while Quartz
 * uses 1; Calendar uses {@value Calendar#SUNDAY}).
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class DayOfWeekRangeAdapter implements AtomicRuleAdapter {

    private static final int NB_DAYS_IN_WEEK = 7;
    private static final int MIN = Calendar.SUNDAY;
    private static final int MAX = Calendar.SATURDAY;

    private final int shift;

    public DayOfWeekRangeAdapter(int sunday) {
        this.shift = Calendar.SUNDAY - sunday;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.keyboardplaying.cron.parser.AtomicRuleAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.SingleValueRule)
     */
    @Override
    public CronRule adapt(SingleValueRule rule) {
        return new SingleValueRule(shift(rule.getValue()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.keyboardplaying.cron.parser.AtomicRuleAdapter#adapt(org.keyboardplaying.cron.expression
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
            result = new MultipleRule(new RangeRule(MIN, max), new RangeRule(min, MAX));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.keyboardplaying.cron.parser.AtomicRuleAdapter#adapt(org.keyboardplaying.cron.expression
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

    private int shift(int value) {
        int shifted = (value + shift) % NB_DAYS_IN_WEEK;
        return shifted < MIN ? shifted + NB_DAYS_IN_WEEK : shifted;
    }
}
