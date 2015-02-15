package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;

/**
 * Tests {@link AnyValueRule}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class AnyValueRuleTest {

    private CronRule field = new AnyValueRule();

    /** Tests that any passed value is accepted. */
    @Test
    public void testRule() {
        // negatives may be restricted in the future
        assertTrue(field.allows(-1));
        assertTrue(field.allows(0));
        assertTrue(field.allows(42));
        assertTrue(field.allows(1337));
    }
}
