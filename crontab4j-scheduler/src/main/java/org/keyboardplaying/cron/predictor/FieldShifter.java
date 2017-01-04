package org.keyboardplaying.cron.predictor;

import java.util.Calendar;

import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.CronRule;

// TODO Javadoc
// package-restricted
class FieldShifter {

    private int calendarField;
    private int min;
    private int max;
    private int ordinal;

    public FieldShifter(int field, int min, int max, int ordinal) {
        this.calendarField = field;
        this.min = min;
        this.max = max;
        this.ordinal = ordinal;
    }

    protected int getMax(Calendar cal, CronRule rule) {
        assert !rule.hasMax() || rule.getMax() <= max;
        return rule.hasMax() ? rule.getMax() : max;
    }

    public boolean allowsField(Calendar cal, CronExpression cron, Field cronField) {
        return cron.get(cronField).allows(cal.get(calendarField));
    }

    public Calendar shift(Calendar cal, CronExpression cron, Field exprField) {
        // define a recursive sub method to avoid getting max and rule on each iteration
        return shift(cal, cron, cron.get(exprField));
    }

    private Calendar shift(Calendar cal, CronExpression cron, CronRule rule) {
        final int localMax = getMax(cal, rule);
        int value = cal.get(calendarField);
        do {
            value++;
            if (value > localMax) {
                Calendar next = shiftUpper(cal, cron);
                if (next == null) {
                    return null;
                }
                return rule.allows(next.get(calendarField)) ? next : shift(cal, cron, rule);
            }
        } while (!rule.allows(value));

        Calendar next = resetLowers(cal);
        next.set(calendarField, value);

        return next;
    }

    protected final Calendar shiftUpper(Calendar cal, CronExpression cron) {
        if (ordinal == 0) {
            // no upper, no possible result
            return null;
        }
        return PredictorField.values()[ordinal - 1].shift(cal, cron);
    }

    protected void reset(Calendar cal) {
        cal.set(calendarField, min);
    }

    protected final Calendar resetLowers(Calendar cal) {
        PredictorField[] values = PredictorField.values();
        for (int i = ordinal + 1; i < values.length; i++) {
            values[i].reset(cal);
        }

        return cal;
    }
}
