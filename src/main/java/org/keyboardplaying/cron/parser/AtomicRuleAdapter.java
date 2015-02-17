package org.keyboardplaying.cron.parser;

import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * A utility to adapt the rules to compensate for differences between the syntaxes.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public interface AtomicRuleAdapter {
    CronRule adapt(SingleValueRule rule);

    CronRule adapt(RangeRule rule);

    CronRule adapt(RepeatRule rule);
}
