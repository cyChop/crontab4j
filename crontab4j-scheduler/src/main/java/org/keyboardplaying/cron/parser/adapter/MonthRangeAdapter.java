package org.keyboardplaying.cron.parser.adapter;

import java.util.Calendar;

/**
 * A utility for adapting the month range for the computer.
 * <p/>
 * The number is not identical depending on the syntax (Unix and Quartz uses 1 as January; Calendar uses
 * {@value Calendar#JANUARY}).
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class MonthRangeAdapter extends ShiftRangeAdapter {

    /**
     * Creates a new instance.
     *
     * @param january
     *            the integer representation of January in the origin expression
     */
    public MonthRangeAdapter(int january) {
        super(Calendar.JANUARY, Calendar.DECEMBER, january, Calendar.JANUARY);
    }
}
