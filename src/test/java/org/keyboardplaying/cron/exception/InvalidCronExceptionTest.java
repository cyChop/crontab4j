package org.keyboardplaying.cron.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link InvalidCronException}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class InvalidCronExceptionTest {

    /** Tests the creation of an exception and the getting of its attributes. */
    @Test
    public void testException() {
        InvalidCronException e = new InvalidCronException("somecron");
        assertEquals("somecron", e.getCron());
    }
}
