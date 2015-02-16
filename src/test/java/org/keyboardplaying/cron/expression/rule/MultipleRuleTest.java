package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link MultipleRule}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class MultipleRuleTest {

    // 0-10/2,15,30-45
    private CronRule rule = new MultipleRule(new RepeatRule(0, 10, 2), new SingleValueRule(15),
            new RangeRule(30, 45));

    /** Tests {@link MultipleRule#hasMax()} and {@link MultipleRule#getMax()} methods. */
    @Test
    public void testMax() {
        assertTrue(rule.hasMax());
        assertEquals(45, rule.getMax());

        CronRule withAnyRule = new MultipleRule(new RepeatRule(0, 10, 2), new SingleValueRule(15),
            new AnyValueRule());
        assertFalse(withAnyRule.hasMax());
        assertEquals(Integer.MAX_VALUE, withAnyRule.getMax());
    }

    /** Tests that the passed values match at least one of the rules to be accepted. */
    @Test
    public void testRule() {
        assertFalse(rule.allows(-1));
        assertTrue(rule.allows(0));
        assertTrue(rule.allows(2));
        assertFalse(rule.allows(11));
        assertTrue(rule.allows(15));
        assertTrue(rule.allows(42));
        assertFalse(rule.allows(1337));

    }
}
