package org.keyboardplaying.cron.parser.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests {@link ShiftRangeAdapter}.
 * <p/>
 * {@link DayOfWeekRangeAdapter} and {@link MonthRangeAdapter} will <em>not</em> be tested <em>per
 * se</em> since they are nothing more than parameterized {@link ShiftRangeAdapter} implementations.
 *
 * @author Cyrille Chopelet
 */
public class ShiftRangeAdapterTest {

    private static final int UNIX_SUNDAY = 0;
    private ShiftRangeAdapter adptr = new DayOfWeekRangeAdapter(UNIX_SUNDAY);

    /** Tests the adaptation of a {@link SingleValueRule}. */
    @Test
    public void testSingleValueAdaptation() {
        CronRule rule;

        rule = adptr.adapt(new SingleValueRule(0));
        assertTrue(rule instanceof SingleValueRule);
        assertEquals(Calendar.SUNDAY, ((SingleValueRule) rule).getValue());

        rule = adptr.adapt(new SingleValueRule(7));
        assertTrue(rule instanceof SingleValueRule);
        assertEquals(Calendar.SUNDAY, ((SingleValueRule) rule).getValue());
    }

    /** Tests the adaptation of a {@link RangeRule} in case it requires only shifting the limits. */
    @Test
    public void testRangeSimpleAdaptation() {
        CronRule rule = adptr.adapt(new RangeRule(1, 5));

        assertTrue(rule instanceof RangeRule);
        assertEquals(Calendar.MONDAY, ((RangeRule) rule).getMin());
        assertEquals(Calendar.FRIDAY, ((RangeRule) rule).getMax());
    }

    /**
     * Tests the adaptation of a {@link RangeRule} in case it requires splitting into two ranges (limits overlapping).
     */
    @Test
    public void testRangeComplexAdaptation() {
        CronRule rule = adptr.adapt(new RangeRule(6, 7));

        assertTrue(rule instanceof MultipleRule);
        assertTrue(rule.allows(Calendar.SUNDAY));
        assertFalse(rule.allows(Calendar.MONDAY));
        assertFalse(rule.allows(Calendar.TUESDAY));
        assertFalse(rule.allows(Calendar.WEDNESDAY));
        assertFalse(rule.allows(Calendar.THURSDAY));
        assertFalse(rule.allows(Calendar.FRIDAY));
        assertTrue(rule.allows(Calendar.SATURDAY));
    }

    /** Tests the adaptation of a {@link RepeatRule} in case it requires only shifting the limits. */
    @Test
    public void testRepeatSimpleAdaptation() {
        CronRule rule = adptr.adapt(new RepeatRule(1, 5, 2));

        assertTrue(rule instanceof RepeatRule);
        assertEquals(Calendar.MONDAY, ((RepeatRule) rule).getMin());
        assertEquals(Calendar.FRIDAY, ((RepeatRule) rule).getMax());
        assertEquals(2, ((RepeatRule) rule).getStep());
    }

    /** Tests the adaptation of a {@link RangeRule} in case it requires more complex processing (limits overlapping). */
    @Test
    public void testRepeatComplexAdaptation() {
        CronRule rule = adptr.adapt(new RepeatRule(4, 7, 3));

        assertTrue(rule instanceof MultipleRule);
        assertTrue(rule.allows(Calendar.SUNDAY));
        assertFalse(rule.allows(Calendar.MONDAY));
        assertFalse(rule.allows(Calendar.TUESDAY));
        assertFalse(rule.allows(Calendar.WEDNESDAY));
        assertTrue(rule.allows(Calendar.THURSDAY));
        assertFalse(rule.allows(Calendar.FRIDAY));
        assertFalse(rule.allows(Calendar.SATURDAY));
    }
}
