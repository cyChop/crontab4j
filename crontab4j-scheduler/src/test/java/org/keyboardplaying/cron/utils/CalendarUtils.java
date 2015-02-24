package org.keyboardplaying.cron.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Testing utilities for {@link Calendar}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public final class CalendarUtils {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private CalendarUtils() {
        throw new AssertionError(
                "org.keyboardplaying.cron.utils.CalendarUtils should not be instanciated");
    }

    /**
     * Null-safe method to create a {@link Calendar} from a formatted {@link String}.
     * <p/>
     * Format template is {@code yyyy-MM-dd'T'HH:mm:ss}.
     *
     * @param source
     *            the formatted {@link String} to parse
     * @return a {@link Calendar}
     */
    public static Calendar parse(String source) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(source));
        return cal;
    }

    /**
     * Null-safe method to display a {@link Calendar} as a formatted {@link String}.
     * <p/>
     * Format template is {@code yyyy-MM-dd'T'HH:mm:ss}.
     *
     * @param cal
     *            the {@link Calendar}
     * @return the formatted {@link String}
     */
    public static String format(Calendar cal) {
        return cal == null ? "null" : "<" + df.format(cal.getTime()) + ">";
    }
}
