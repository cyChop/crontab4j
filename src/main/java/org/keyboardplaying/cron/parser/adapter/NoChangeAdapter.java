package org.keyboardplaying.cron.parser.adapter;

import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * An implementation for rules needing no adaptation.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class NoChangeAdapter implements RangeAdapter {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.SingleValueRule)
     */
    @Override
    public CronRule adapt(SingleValueRule rule) {
        return rule;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.RangeRule)
     */
    @Override
    public CronRule adapt(RangeRule rule) {
        return rule;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.keyboardplaying.cron.parser.RangeAdapter#adapt(org.keyboardplaying.cron.expression
     * .rule.RepeatRule)
     */
    @Override
    public CronRule adapt(RepeatRule rule) {
        return rule;
    }
}
