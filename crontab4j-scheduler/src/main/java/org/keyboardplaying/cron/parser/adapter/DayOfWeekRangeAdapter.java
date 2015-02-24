package org.keyboardplaying.cron.parser.adapter;

import java.util.Calendar;

/**
 * A utility for adapting the day range for the computer.
 * <p/>
 * The number is not identical depending on the syntax (Unix uses 0 or 7 as Sunday, while Quartz
 * uses {@code 1}; Calendar uses {@value Calendar#SUNDAY}).
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class DayOfWeekRangeAdapter extends ShiftRangeAdapter {

    /**
     * Creates a new instance.
     *
     * @param sunday
     *            the integer representation of Sunday in the origin expression
     */
    public DayOfWeekRangeAdapter(int sunday) {
        super(Calendar.SUNDAY, Calendar.SATURDAY, sunday, Calendar.SUNDAY);
    }
}
