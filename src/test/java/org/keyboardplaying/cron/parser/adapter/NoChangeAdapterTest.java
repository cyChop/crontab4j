package org.keyboardplaying.cron.parser.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests {@link DayOfWeekRangeAdapter}.
 *
 * @author Cyrille Chopelet
 */
// XXX Javadoc
public class NoChangeAdapterTest {

    private NoChangeAdapter adptr = new NoChangeAdapter();

    @Test
    public void testSingleValueAdaptation() {
        SingleValueRule rule = new SingleValueRule(0);
        assertEquals(rule, adptr.adapt(rule));
    }

    @Test
    public void testRangeAdaptation() {
        RangeRule rule = new RangeRule(1, 15);
        assertEquals(rule, adptr.adapt(rule));
    }

    @Test
    public void testRepeatAdaptation() {
        RepeatRule rule = new RepeatRule(0, 59, 15);
        assertEquals(rule, adptr.adapt(rule));
    }
}
