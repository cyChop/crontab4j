package org.keyboardplaying.cron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests {@link Objects}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class ObjectsTest {

    /** Tests {@link Objects#equals(java.lang.Object, java.lang.Object)}. */
    @Test
    public void testEquals() {
        String str1 = "Hello, world!";
        String str2 = new String(str1);

        assertTrue(Objects.equals(null, null));
        assertFalse(Objects.equals(null, str2));
        assertFalse(Objects.equals(str1, null));
        assertTrue(Objects.equals(str1, str1));
        assertTrue(Objects.equals(str1, str2));
        assertFalse(Objects.equals(str1, "str2"));
    }

    /** Tests {@link Objects#requireNonNull(java.lang.Object)} with null argument. */
    @Test(expected = NullPointerException.class)
    public void testRequireNonNullNoMessage1() {
        Objects.requireNonNull(null);
    }

    /** Tests {@link Objects#requireNonNull(java.lang.Object)} with non-null argument. */
    @Test
    public void testRequireNonNullNoMessage2() {
        assertEquals("Hello, world!", Objects.requireNonNull("Hello, world!"));
    }

    /**
     * Tests {@link Objects#requireNonNull(java.lang.Object, java.lang.String)} with null argument.
     */
    @Test
    public void testRequireNonNullMessage1() {
        try {
            Objects.requireNonNull(null, "a message");
            fail();
        } catch (NullPointerException e) {
            assertEquals("a message", e.getMessage());
        }
    }

    /**
     * Tests {@link Objects#requireNonNull(java.lang.Object, java.lang.String)} with non-null
     * argument.
     */
    @Test
    public void testRequireNonNullMessage2() {
        assertEquals("Hello, world!", Objects.requireNonNull("Hello, world!", "a message"));
    }
}
