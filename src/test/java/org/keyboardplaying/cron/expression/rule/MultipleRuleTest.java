package org.keyboardplaying.cron.expression.rule;

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
    private CronRule field = new MultipleRule(new RepeatRule(0, 10, 2), new SingleValueRule(15),
            new RangeRule(30, 45));

    /** Tests that the passed values match at least one of the rules to be accepted. */
    @Test
    public void testRule() {
        assertFalse(field.allows(-1));
        assertTrue(field.allows(0));
        assertTrue(field.allows(2));
        assertFalse(field.allows(11));
        assertTrue(field.allows(15));
        assertTrue(field.allows(42));
        assertFalse(field.allows(1337));
    }
}
