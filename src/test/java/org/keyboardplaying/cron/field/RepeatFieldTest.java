package org.keyboardplaying.cron.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RepeatField}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class RepeatFieldTest {

    private RepeatField field = new RepeatField(42, 1337, 2);

    /** Tests the getters. */
    @Test
    public void testGetters() {
        assertEquals(42, field.getMin());
        assertEquals(1337, field.getMax());
        assertEquals(2, field.getStep());
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
        assertFalse(field.allows(1664));
        // Repeat KO
        assertFalse(field.allows(1515));
    }
}
