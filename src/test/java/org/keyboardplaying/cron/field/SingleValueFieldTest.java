package org.keyboardplaying.cron.field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link SingleValueField}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class SingleValueFieldTest {

    private CronField field = new SingleValueField(42);

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
