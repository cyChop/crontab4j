package org.keyboardplaying.cron.parser.adapter;

import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * A utility to adapt the rules to compensate for differences between the syntaxes.
 * <p/>
 * This interface is for instance especially useful for the day of week, which depends on both the
 * CRON syntax and the Java implementation.
 * <p/>
 * Only atomic range rules should require adaptation:
 * <ul>
 * <li>Any-value rules do not need adaptation since they accept any value anyway.</li>
 * <li>Composed rules are adapted through adaptation of all its atomic components.</li>
 * </ul>
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public interface AtomicRangeAdapter {

    /**
     * Computes a change of base of the supplied rule from its original syntax to the system base.
     *
     * @param rule
     *            the rule to adapt
     * @return the adapted rule
     */
    CronRule adapt(SingleValueRule rule);

    /**
     * Computes a change of base of the supplied rule from its original syntax to the system base.
     *
     * @param rule
     *            the rule to adapt
     * @return the adapted rule
     */
    CronRule adapt(RangeRule rule);

    /**
     * Computes a change of base of the supplied rule from its original syntax to the system base.
     *
     * @param rule
     *            the rule to adapt
     * @return the adapted rule
     */
    CronRule adapt(RepeatRule rule);
}
