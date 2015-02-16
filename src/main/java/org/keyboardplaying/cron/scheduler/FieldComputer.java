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

        private FieldShifter dowShifter = new FieldShifter(Calendar.DAY_OF_WEEK, 1, 7, ordinal()) {
            @Override
            public Calendar shift(Calendar cal, CronExpression expr, Field exprField) {
                CronRule rule = expr.get(exprField);
                Calendar next = resetLowers((Calendar) cal.clone());

                // at most 7 iterations
                do {
                    next.add(Calendar.DATE, 1);
                } while (!rule.allows(next.get(Calendar.DAY_OF_WEEK)));

                // The month may have shifted, ensure the constraints are still OK
                if (next.get(Calendar.MONTH) != cal.get(Calendar.MONTH) && !MONTH.allows(next, expr)
                        || next.get(Calendar.YEAR) != cal.get(Calendar.YEAR)
                        && !YEAR.allows(next, expr)) {
                    next = shiftUpper(cal, expr);
                    if (next != null && !allowsField(next, expr, Field.DAY_OF_WEEK)) {
                        next = shift(next, expr, exprField);
                    }
                }
                return next;
            }
        };

        private FieldShifter domShifter = new FieldShifter(Calendar.DAY_OF_MONTH, 1, 31, ordinal()) {
            @Override
            protected int getMax(Calendar cal, CronRule rule) {
                if (rule.hasMax()) {
                    int ruleMax = rule.getMax();
                    int caldMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    return ruleMax < caldMax ? ruleMax : caldMax;
                } else {
                    return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                }
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

            case BOTH_OR:
                Calendar domNext = shiftDayOfMonth((Calendar) cal.clone(), expr);
                Calendar dowNext = shiftDayOfWeek((Calendar) cal.clone(), expr);

                if (domNext == null) {
                    next = dowNext;
                } else if (dowNext == null) {
                    next = domNext;
                } else {
                    next = dowNext.before(domNext) ? dowNext : domNext;
                }

                break;

            case BOTH_AND:
                final CronRule dowRule = expr.get(Field.DAY_OF_WEEK);
                next = cal;
                do {
                    next = shiftDayOfMonth(next, expr);
                } while (next != null && !dowRule.allows(next.get(Calendar.DAY_OF_WEEK)));
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
    private FieldShifter shifter;

    private FieldComputer() {
    }

    private FieldComputer(Field cronField, int calendarField, int min, int max) {
        this.cronField = cronField;
        this.shifter = new FieldShifter(calendarField, min, max, ordinal());
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
