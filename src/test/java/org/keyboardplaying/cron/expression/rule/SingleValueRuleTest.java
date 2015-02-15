package org.keyboardplaying.cron.expression.rule;

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

    private CronRule field = new SingleValueRule(42);

    /** Tests that the correct value is accepted. */
    @Test
    public void testAccept() {
        assertTrue(field.allows(42));
    }

    /** Tests that calues other than the selected one are rejected. */
    @Test
    public void testReject() {
        assertFalse(field.allows(-1));
        assertFalse(field.allows(0));
        assertFalse(field.allows(1337));
    }
}
