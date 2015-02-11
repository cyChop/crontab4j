package org.keyboardplaying.cron.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link CronSegment}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronSegmentTest {

    /** Tests a non-restricted segment. */
    @Test
    public void testNonRestricted() {
        CronSegment segment = new CronSegment();
        segment.setMin(0);
        segment.setMax(59);

        assertTrue(segment.isAllowed(0));
        assertTrue(segment.isAllowed(42));
        assertTrue(segment.isAllowed(59));
        assertFalse(segment.isAllowed(60));
        assertFalse(segment.isAllowed(-1));
    }

    /** Tests a segment with range limits and repeat condition. */
    @Test
    public void testRepeat() {
        CronSegment segment = new CronSegment();
        segment.setMin(0);
        segment.setMax(42);
        segment.setRepeat(2);

        assertTrue(segment.isAllowed(0));
        assertTrue(segment.isAllowed(42));
        assertFalse(segment.isAllowed(7));
        assertFalse(segment.isAllowed(44));
        assertFalse(segment.isAllowed(-2));
    }
}
