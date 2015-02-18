package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;
import java.util.Objects;

import org.keyboardplaying.cron.expression.CronExpression;

/**
 * This class provides the capability to compute the next time a CRON expression should be
 * triggered.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronComputer {

    /**
     * Returns the next time the CRON expression will be triggered.
     * <p/>
     * If the CRON will not be triggered again in the future, this method returns {@code null}.
     *
     * @param expr
     *            the CRON expression to use to evaluate
     * @return a {@link Calendar} representing the next time the expression will be triggered, or
     *            {@code null} if no next occurrence can be found
     * @throws NullPointerException
     *            if the supplied expression is {@code null}
     *
     * @see #getNextOccurrence(org.keyboardplaying.cron.expression.CronExpression,
     *            java.util.Calendar)
     */
    public Calendar getNextOccurrence(CronExpression expr) {
        return getNextOccurrence(expr, null);
    }

    /**
     * Returns the next time the CRON expression will be triggered after the supplied calendar.
     * <p/>
     * If the CRON will not be triggered again after the argument, this method returns {@code null}.
     * <p/>
     * If the supplied time argument is {@code null}, current time will be used instead.
     *
     * @param cal
     *            the time base for searching next occurrence
     * @param expr
     *            the CRON expression to use to evaluate
     * @return a {@link Calendar} representing the next time the expression will be triggered, or
     *            {@code null} if no next occurrence can be found
     * @throws NullPointerException
     *            if the supplied expression is {@code null}
     */
    public Calendar getNextOccurrence(CronExpression expr, Calendar cal) {
        Objects.requireNonNull(expr, "A CRON must be supplied");

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
