package org.keyboardplaying.cron.expression.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RepeatRule}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class RepeatRuleTest {

    private RepeatRule field = new RepeatRule(42, 1337, 3);

    /** Tests the getters. */
    @Test
    public void testGetters() {
        assertEquals(42, field.getMin());
        assertEquals(1337, field.getMax());
        assertEquals(3, field.getStep());
    }

    /**
     * Ensures the range limits are accepted.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testLimits() {
        // Repeat OK
        assertTrue(field.allows(42));
        // Repeat KO
        assertFalse(field.allows(1337));
    }

    /**
     * Ensures elements inbetween the range limit are accepted
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testRepeatBody() {
        // Repeat OK
        assertTrue(field.allows(420));
        assertTrue(field.allows(423));
        // Repeat KO
        assertFalse(field.allows(421));
    }

    /**
     * Ensures values below the lower limit are rejected.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testBelowRange() {
        // Repeat OK
        assertFalse(field.allows(0));
        // Repeat KO
        assertFalse(field.allows(-1));
        assertFalse(field.allows(1));
    }

    /**
     * Ensures values above the upper limit are rejected.
     * <p/>
     * The test will include at least one value matching the repetition and one not matching it.
     */
    @Test
    public void testAboveRange() {
        // Repeat OK
        assertFalse(field.allows(1515));
        // Repeat KO
        assertFalse(field.allows(1664));
    }

    /**
     * Tests a case month case like {@code * /10}, which should be equivalent to {@code 1,11,21,31}.
     */
    @Test
    public void testBasicCase() {
        RepeatRule field = new RepeatRule(1, 31, 10);
        // Repeat OK
        assertTrue(field.allows(1));
        assertTrue(field.allows(11));
        assertTrue(field.allows(21));
        assertTrue(field.allows(31));
        // Repeat KO
        assertFalse(field.allows(9));
    }

    /** Tests the behavior of the constructor with incorrect arguments. */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConstructorArguments() {
        new RepeatRule(1, 0, 2);
    }

}
