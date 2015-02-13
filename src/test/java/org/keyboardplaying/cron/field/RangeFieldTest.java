package org.keyboardplaying.cron.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RangeField}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class RangeFieldTest {

    private RangeField field = new RangeField(42, 1337);

    /** Tests the getters. */
    @Test
    public void testGetters() {
        assertEquals(42, field.getMin());
        assertEquals(1337, field.getMax());
    }

    /** Ensures the range limits are accepted. */
    @Test
    public void testLimits() {
        assertTrue(field.allows(42));
        assertTrue(field.allows(1337));
    }

    /** Ensures elements inbetween the range limit are accepted */
    @Test
    public void testRangeBody() {
        assertTrue(field.allows(420));
    }

    /** Ensures values below the lower limit are rejected. */
    @Test
    public void testBelowRange() {
        assertFalse(field.allows(-1));
        assertFalse(field.allows(0));
        assertFalse(field.allows(41));
    }

    /** Ensures values above the upper limit are rejected. */
    @Test
    public void testAboveRange() {
        assertFalse(field.allows(1338));
        assertFalse(field.allows(Integer.MAX_VALUE));
    }
}
