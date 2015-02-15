package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;

import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.CronRule;

// TODO Javadoc
// package-restricted
enum FieldComputer {

    //
    YEAR(Field.YEAR, Calendar.YEAR, 1970, 9999),
    //
    MONTH(Field.MONTH, Calendar.MONTH, Calendar.JANUARY, Calendar.DECEMBER),
    //
    DAY {

        private Shifter dowShifter = new Shifter(Calendar.DAY_OF_WEEK, 1, 7, ordinal()) {
            @Override
            public Calendar shift(Calendar cal, CronExpression expr, Field exprField) {
                CronRule rule = expr.get(exprField);
                Calendar next = resetLowers((Calendar) cal.clone());

                // at most 7 iterations
                do {
                    next.add(Calendar.DATE, 1);
                } while (!rule.allows(next.get(Calendar.DAY_OF_WEEK)));

                // The month may have shifted, ensure the constraints are still OK
                if (next.get(Calendar.MONTH) != cal.get(Calendar.MONTH)
                        || next.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
                    // FIXME method should be able to return null
                    if (!MONTH.allows(next, expr) || !YEAR.allows(next, expr)) {
                        next = shiftUpper(next, expr);
                    }
                    if (!allowsField(next, expr, Field.DAY_OF_WEEK)) {
                        next = shift(next, expr, exprField);
                    }
                }
                return next;
            }
        };

        private Shifter domShifter = new Shifter(Calendar.DAY_OF_MONTH, 1, 31, ordinal()) {
            @Override
            protected int getMax(Calendar cal) {
                return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        };

        @Override
        public boolean allows(Calendar cal, CronExpression expr) {
            return domShifter.allowsField(cal, expr, Field.DAY_OF_MONTH)
                    && dowShifter.allowsField(cal, expr, Field.DAY_OF_WEEK);
        }

        @Override
        public Calendar shift(Calendar cal, CronExpression expr) {
            Calendar next = null;
            switch (expr.getDayConstraint()) {

            case NONE:
            case MONTH:
                next = shiftDayOfMonth(cal, expr);
                break;

            case WEEK:
                next = shiftDayOfWeek(cal, expr);
                break;

            case BOTH_AND:
            case BOTH_OR:
                CronRule dowRule = expr.get(Field.DAY_OF_WEEK);
                next = cal;
                do {
                    next = shiftDayOfMonth(next, expr);
                } while (next != null && dowRule.allows(next.get(Calendar.DAY_OF_WEEK)));
            }
            return next;
        }

        private Calendar shiftDayOfMonth(Calendar cal, CronExpression expr) {
            return domShifter.shift(cal, expr, Field.DAY_OF_MONTH);
        }

        private Calendar shiftDayOfWeek(Calendar cal, CronExpression expr) {
            return dowShifter.shift(cal, expr, Field.DAY_OF_WEEK);
        }

        @Override
        public void reset(Calendar cal) {
            domShifter.reset(cal);
        }
    },
    //
    HOUR(Field.HOUR, Calendar.HOUR_OF_DAY, 0, 23),
    //
    MINUTE(Field.MINUTE, Calendar.MINUTE, 0, 59),
    //
    SECOND(Field.SECOND, Calendar.SECOND, 0, 59);

    private Field cronField;
    private Shifter shifter;

    private FieldComputer() {
    }

    private FieldComputer(Field cronField, int calendarField, int min, int max) {
        this.cronField = cronField;
        this.shifter = new Shifter(calendarField, min, max, ordinal());
    }

    public boolean allows(Calendar cal, CronExpression expr) {
        return shifter.allowsField(cal, expr, cronField);
    }

    public Calendar shift(Calendar cal, CronExpression expr) {
        return shifter.shift(cal, expr, cronField);
    }

    public void reset(Calendar cal) {
        shifter.reset(cal);
    }
}
