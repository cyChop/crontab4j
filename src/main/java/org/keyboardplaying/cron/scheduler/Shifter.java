package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;

import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.CronRule;

// TODO Javadoc
// package-restricted
class Shifter {

    private int calendarField;
    private int min;
    private int max;
    private int ordinal;

    public Shifter(int field, int min, int max, int ordinal) {
        this.calendarField = field;
        this.min = min;
        this.max = max;
        this.ordinal = ordinal;
    }

    protected int getMax(Calendar cal) {
        return max;
    }

    public boolean allowsField(Calendar cal, CronExpression expr, Field cronField) {
        return expr.get(cronField).allows(cal.get(calendarField));
    }

    public Calendar shift(Calendar cal, CronExpression expr, Field exprField) {
        return shift(cal, expr, expr.get(exprField), getMax(cal));
    }

    private Calendar shift(Calendar cal, CronExpression expr, CronRule rule, int max) {
        int value = cal.get(calendarField);
        do {
            value++;
            if (value > max) {
                Calendar next = shiftUpper(cal, expr);
                if (next == null) {
                    return null;
                } else {
                    return rule.allows(next.get(calendarField)) ? next
                            : shift(cal, expr, rule, max);
                }
            }
        } while (!rule.allows(value));

        Calendar next = resetLowers(cal);
        next.set(calendarField, value);

        return next;
    }

    protected final Calendar shiftUpper(Calendar cal, CronExpression expr) {
        if (ordinal == 0) {
            // no upper, no possible result
            return null;
        } else {
            return FieldComputer.values()[ordinal - 1].shift(cal, expr);
        }
    }

    protected void reset(Calendar cal) {
        cal.set(calendarField, min);
    }

    protected final Calendar resetLowers(Calendar cal) {
        FieldComputer[] values = FieldComputer.values();
        for (int i = ordinal + 1; i < values.length; i++) {
            values[i].reset(cal);
        }

        return cal;
    }
}
