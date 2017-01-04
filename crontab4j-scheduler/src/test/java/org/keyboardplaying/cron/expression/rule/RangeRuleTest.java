package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RangeRule}.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class RangeRuleTest {

    private RangeRule rule = new RangeRule(42, 1337);

    /**
     * Tests {@link RangeRule#hasMax()} and {@link RangeRule#getMax()} methods.
     */
    @Test
    public void testMax() {
        assertTrue(rule.hasMax());
        assertEquals(1337, rule.getMax());
    }

    /**
     * Tests the getters.
     */
    @Test
    public void testGetters() {
        assertEquals(42, rule.getMin());
        assertEquals(1337, rule.getMax());
    }

    /**
     * Ensures the range limits are accepted.
     */
    @Test
    public void testLimits() {
        assertTrue(rule.allows(42));
        assertTrue(rule.allows(1337));
    }

    /**
     * Ensures elements in-between the range limit are accepted
     */
    @Test
    public void testRangeBody() {
        assertTrue(rule.allows(420));
    }

    /**
     * Ensures values below the lower limit are rejected.
     */
    @Test
    public void testBelowRange() {
        assertFalse(rule.allows(-1));
        assertFalse(rule.allows(0));
        assertFalse(rule.allows(41));
    }

    /**
     * Ensures values above the upper limit are rejected.
     */
    @Test
    public void testAboveRange() {
        assertFalse(rule.allows(1338));
        assertFalse(rule.allows(Integer.MAX_VALUE));
    }

    /**
     * Tests the behavior of the constructor with incorrect arguments.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConstructorArguments() {
        new RangeRule(1, 0);
    }
}
