package org.keyboardplaying.cron.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.keyboardplaying.cron.expression.CronExpression.Builder;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests@link CronExpression.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronExpressionTest {

    /** Tests the creation of a {@link CronExpression} and the setting of the fields. */
    @Test
    public void test() {
        CronRule any = new AnyValueRule();
        CronRule roundMinutes = new SingleValueRule(0);
        CronRule everyQuarter = new RepeatRule(0, 59, 15);
        CronRule weekDaysOnly = new RangeRule(1, 5);
        CronRule onlyWorkHours = new MultipleRule(new RangeRule(8, 12), new RangeRule(14, 18));

        CronExpression expr = Builder.create().set(Field.SECOND, roundMinutes)
                .set(Field.MINUTE, everyQuarter).set(Field.HOUR, onlyWorkHours)
                .set(Field.DAY_OF_MONTH, any).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, weekDaysOnly).set(Field.YEAR, any).set(DayConstraint.NONE)
                .build();

        assertEquals(roundMinutes, expr.get(Field.SECOND));
        assertEquals(everyQuarter, expr.get(Field.MINUTE));
        assertEquals(onlyWorkHours, expr.get(Field.HOUR));
        assertEquals(any, expr.get(Field.DAY_OF_MONTH));
        assertEquals(any, expr.get(Field.MONTH));
        assertEquals(weekDaysOnly, expr.get(Field.DAY_OF_WEEK));
        assertEquals(any, expr.get(Field.YEAR));
        assertEquals(DayConstraint.NONE, expr.getDayConstraint());
    }

    /** Tests the creation of a {@link CronExpression and the setting} with a missing rule. */
    @Test(expected = IllegalStateException.class)
    public void testIncompleteRules() {
        CronRule any = new AnyValueRule();

        Builder.create().set(Field.SECOND, any).set(Field.MINUTE, any).set(Field.HOUR, any)
                // .set(Field.DAY_OF_MONTH, any)
                .set(Field.MONTH, any).set(Field.DAY_OF_WEEK, any).set(Field.YEAR, any)
                .set(DayConstraint.NONE).build();
    }

    /** Tests the creation of a {@link CronExpression and the setting} without a day constraint. */
    @Test(expected = IllegalStateException.class)
    public void testNoDayConstraint() {
        CronRule any = new AnyValueRule();

        Builder.create().set(Field.SECOND, any).set(Field.MINUTE, any).set(Field.HOUR, any)
                .set(Field.DAY_OF_MONTH, any).set(Field.MONTH, any).set(Field.DAY_OF_WEEK, any)
                .set(Field.YEAR, any)
                // .set(DayConstraint.NONE)
                .build();
    }
}
