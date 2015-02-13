package org.keyboardplaying.cron.field;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link AnyValueField}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class AnyValueFieldTest {

    private CronField field = new AnyValueField();

    /** Tests that any passed value is accepted. */
    @Test
    public void testField() {
        // negatives may be restricted in the future
        assertTrue(field.allows(-1));
        assertTrue(field.allows(0));
        assertTrue(field.allows(42));
        assertTrue(field.allows(1337));
    }
}
