package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RepeatRule}.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class RepeatRuleTest {

    private RepeatRule rule = new RepeatRule(42, 1337, 3);

    /**
     * Tests {@link RepeatRule#hasMax()} and {@link RepeatRule#getMax()} methods.
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
        assertEquals(3, rule.getStep());
    }

    /**
     * Ensures the range limits are accepted.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testLimits() {
        // Repeat OK
        assertTrue(rule.allows(42));
        // Repeat KO
        assertFalse(rule.allows(1337));
    }

    /**
     * Ensures elements inbetween the range limit are accepted
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testRepeatBody() {
        // Repeat OK
        assertTrue(rule.allows(420));
        assertTrue(rule.allows(423));
        // Repeat KO
        assertFalse(rule.allows(421));
    }

    /**
     * Ensures values below the lower limit are rejected.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testBelowRange() {
        // Repeat OK
        assertFalse(rule.allows(0));
        // Repeat KO
        assertFalse(rule.allows(-1));
        assertFalse(rule.allows(1));
    }

    /**
     * Ensures values above the upper limit are rejected.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testAboveRange() {
        // Repeat OK
        assertFalse(rule.allows(1515));
        // Repeat KO
        assertFalse(rule.allows(1664));
    }

    /**
     * Tests a case month case like {@code * /10}, which should be equivalent to {@code 1,11,21,31}.
     */
    @Test
    public void testBasicCase() {
        RepeatRule r = new RepeatRule(1, 31, 10);
        // Repeat OK
        assertTrue(r.allows(1));
        assertTrue(r.allows(11));
        assertTrue(r.allows(21));
        assertTrue(r.allows(31));
        // Repeat KO
        assertFalse(r.allows(9));
    }

    /**
     * Tests the behavior of the constructor with incorrect arguments.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConstructorArguments() {
        new RepeatRule(1, 0, 2);
    }
}
