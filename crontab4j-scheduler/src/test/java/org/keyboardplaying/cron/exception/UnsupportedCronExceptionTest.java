package org.keyboardplaying.cron.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link UnsupportedCronException}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class UnsupportedCronExceptionTest {

    /** Tests the creation of an exception and the getting of its attributes. */
    @Test
    public void testException() {
        UnsupportedCronException e = new UnsupportedCronException("somecron", true);
        assertEquals("somecron", e.getCron());
        assertTrue(e.isValid());
    }

    /** Tests the creation of an exception with a cause and the getting of its attributes. */
    @Test
    public void testExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Field is incorrect.");
        UnsupportedCronException e = new UnsupportedCronException("somecron", cause);
        assertEquals("somecron", e.getCron());
        assertEquals(cause, e.getCause());
        assertFalse(e.isValid());
    }
}
