package org.keyboardplaying.cron.exception;

import static org.junit.Assert.assertEquals;
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
        assertTrue("somecron", e.isValid());
    }
}
