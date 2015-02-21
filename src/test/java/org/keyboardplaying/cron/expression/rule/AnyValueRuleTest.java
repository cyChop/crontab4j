package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link AnyValueRule}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class AnyValueRuleTest {

    private CronRule rule = new AnyValueRule();

    /** Tests {@link AnyValueRule#hasMax()} and {@link AnyValueRule#getMax()} methods. */
    @Test
    public void testMax() {
        assertFalse(rule.hasMax());
        assertEquals(Integer.MAX_VALUE, rule.getMax());
    }

    /** Tests that any passed value is accepted. */
    @Test
    public void testRule() {
        // negatives may be restricted in the future
        assertTrue(rule.allows(-1));
        assertTrue(rule.allows(0));
        assertTrue(rule.allows(42));
        assertTrue(rule.allows(1337));
    }
}
