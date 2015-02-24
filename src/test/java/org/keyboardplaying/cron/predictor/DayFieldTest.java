package org.keyboardplaying.cron.predictor;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.utils.CalendarUtils;

/**
 * Tests some specific behaviour of {@link PredictorField#DAY}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class DayFieldTest {

    private static final Calendar MATCHING_NONE = parseConstant("2015-02-08T00:00:00");
    private static final Calendar MATCHING_MNTH = parseConstant("2015-02-01T00:00:00");
    private static final Calendar MATCHING_WEEK = parseConstant("2015-02-10T00:00:00");
    private static final Calendar MATCHING_BOTH = parseConstant("2015-02-11T00:00:00");

    /** Tests the allowing of a day in case the constraint is {@link DayConstraint#NONE}. */
    @Test
    public void testAllowsNone() throws ParseException {
        CronExpression cron = createCron(DayConstraint.NONE);
        testAllows(MATCHING_NONE, cron, true);
        testAllows(MATCHING_MNTH, cron, true);
        testAllows(MATCHING_WEEK, cron, true);
        testAllows(MATCHING_BOTH, cron, true);
    }

    /** Tests the allowing of a day in case the constraint is {@link DayConstraint#MONTH}. */
    @Test
    public void testAllowsMonthOnly() throws ParseException {
        CronExpression cron = createCron(DayConstraint.MONTH);
        testAllows(MATCHING_NONE, cron, false);
        testAllows(MATCHING_MNTH, cron, true);
        testAllows(MATCHING_WEEK, cron, false);
        testAllows(MATCHING_BOTH, cron, true);
    }

    /** Tests the allowing of a day in case the constraint is {@link DayConstraint#WEEK}. */
    @Test
    public void testAllowsWeekOnly() throws ParseException {
        CronExpression cron = createCron(DayConstraint.WEEK);
        testAllows(MATCHING_NONE, cron, false);
        testAllows(MATCHING_MNTH, cron, false);
        testAllows(MATCHING_WEEK, cron, true);
        testAllows(MATCHING_BOTH, cron, true);
    }

    /** Tests the allowing of a day in case the constraint is {@link DayConstraint#BOTH_OR}. */
    @Test
    public void testAllowsBothOr() throws ParseException {
        CronExpression cron = createCron(DayConstraint.BOTH_OR);
        testAllows(MATCHING_NONE, cron, false);
        testAllows(MATCHING_MNTH, cron, true);
        testAllows(MATCHING_WEEK, cron, true);
        testAllows(MATCHING_BOTH, cron, true);
    }

    /** Tests the allowing of a day in case the constraint is {@link DayConstraint#BOTH_AND}. */
    @Test
    public void testAllowsBothAnd() throws ParseException {
        CronExpression cron = createCron(DayConstraint.BOTH_AND);
        testAllows(MATCHING_NONE, cron, false);
        testAllows(MATCHING_MNTH, cron, false);
        testAllows(MATCHING_WEEK, cron, false);
        testAllows(MATCHING_BOTH, cron, true);
    }

    /* Testing utility */
    // Init constant
    private static Calendar parseConstant(String source) {
        try {
            return CalendarUtils.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Generate expression with supplied constraint
    private CronExpression createCron(DayConstraint constraint) {
        CronRule any = new AnyValueRule();
        return CronExpression.Builder.create().set(Field.SECOND, any).set(Field.MINUTE, any)
                .set(Field.HOUR, any).set(Field.DAY_OF_MONTH, new RepeatRule(1, 31, 10))
                .set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, new RangeRule(Calendar.MONDAY, Calendar.FRIDAY))
                .set(Field.YEAR, any).set(constraint).build();
    }

    // Test value against CRON
    private void testAllows(Calendar cal, CronExpression cron, boolean allowed) {
        assertThat(PredictorField.DAY.allows(cal, cron), is(allowed));
    }
}
