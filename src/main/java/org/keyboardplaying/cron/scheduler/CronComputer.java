package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;

import org.keyboardplaying.cron.expression.CronExpression;

// TODO Javadoc
/**
 * This class provides the capability to compute the next time a tr
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronComputer {

    private CronExpression expr;

    public CronComputer(CronExpression expr) {
        this.expr = expr;
    }

    public Calendar getNextOccurrence() {
        return getNextOccurrence(null);
    }

    public Calendar getNextOccurrence(Calendar cal) {
        Calendar next = cal == null ? Calendar.getInstance() : (Calendar) cal.clone();
        // next occurrence won't be before next second
        next.set(Calendar.MILLISECOND, 0);
        next.add(Calendar.SECOND, 1);

        for (FieldComputer computer : FieldComputer.values()) {
            if (!computer.allows(next, expr)) {
                next = computer.shift(next, expr);
                if (next == null) {
                    return null;
                }
            }
        }

        return next;
    }
}
