package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests {@link SingleValueRule}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class SingleValueRuleTest {

    private CronRule rule = new SingleValueRule(42);

    /** Tests {@link SingleValueRule#hasMax()} and {@link SingleValueRule#getMax()} methods. */
    @Test
    public void testMax() {
        assertTrue(rule.hasMax());
        assertEquals(42, rule.getMax());
    }

    /** Tests that the correct value is accepted. */
    @Test
    public void testAccept() {
        assertTrue(rule.allows(42));
    }

    /** Tests that calues other than the selected one are rejected. */
    @Test
    public void testReject() {
        assertFalse(rule.allows(-1));
        assertFalse(rule.allows(0));
        assertFalse(rule.allows(1337));
    }
}
