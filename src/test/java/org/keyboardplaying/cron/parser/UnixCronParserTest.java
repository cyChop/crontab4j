package org.keyboardplaying.cron.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Test;
import org.keyboardplaying.cron.exception.UnsupportedCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests {@link UnixCronParser}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class UnixCronParserTest {

    CronSyntacticParser prsr = new UnixCronParser();

    /** Tests {@link UnixCronParser#isValid(java.lang.String)}. */
    @Test
    public void testValidate() {
        assertTrue(prsr.isValid("* * * * *"));
        assertTrue(prsr.isValid("0 0 */2 1-7/2,8-12/2 1-5"));
        assertFalse(prsr.isValid("* * * *"));
        assertFalse(prsr.isValid(null));
    }

    /**
     * Ensures the parser fails with a {@link NullPointerException} if the supplied expression is
     * {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void testParseNull() throws UnsupportedCronException {
        prsr.parse(null);
    }

    /**
     * Ensures the parser fails with a {@link UnsupportedCronException} if the supplied expression does
     * not match the validation regex.
     */
    @Test
    public void testParseInvalid() throws UnsupportedCronException {
        try {
            prsr.parse("* * * */mon *");
            fail();
        } catch (UnsupportedCronException e) {
            assertFalse(e.isValid());
            assertEquals("* * * */mon *", e.getCron());
        }
    }

    /**
     * Ensures the {@link CronExpression} obtained from the parsing of a complex expression is
     * correct.
     */
    @Test
    public void testParse() throws UnsupportedCronException {
        CronExpression expr = prsr.parse("0 * 1-15/2,*/3,31 1/2 1-5");

        CronRule second = expr.get(Field.SECOND);
        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);
        CronRule year = expr.get(Field.YEAR);

        assertEquals(DayConstraint.BOTH_OR, expr.getDayConstraint());

        assertTrue(second instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) second).getValue());

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof AnyValueRule);

        assertTrue(dom instanceof MultipleRule);
        assertTrue(dom.allows(1));
        assertFalse(dom.allows(2));
        assertTrue(dom.allows(3));
        assertTrue(dom.allows(4));
        assertTrue(dom.allows(5));
        assertFalse(dom.allows(6));
        assertTrue(dom.allows(7));
        assertFalse(dom.allows(8));
        // ...
        assertFalse(dom.allows(12));
        assertTrue(dom.allows(13));
        assertFalse(dom.allows(14));
        assertTrue(dom.allows(15));
        assertTrue(dom.allows(16));
        assertFalse(dom.allows(17));
        assertFalse(dom.allows(18));
        assertTrue(dom.allows(19));
        // ...

        assertTrue(month instanceof SingleValueRule);
        assertEquals(Calendar.JANUARY, ((SingleValueRule) month).getValue());

        assertTrue(dow instanceof RangeRule);
        assertEquals(Calendar.MONDAY, ((RangeRule) dow).getMin());
        assertEquals(Calendar.FRIDAY, ((RangeRule) dow).getMax());

        assertTrue(year instanceof AnyValueRule);
    }

    /**
     * Ensures the parsing of {@link CronExpression} obtained from the parsing of an expression
     * using names for months and days of week is correct.
     */
    @Test
    public void testParseWithNames() throws UnsupportedCronException {
        // also test names are case insensitive
        assertTrue(prsr.isValid("* * * JaN mon-FRI"));
        CronExpression expr = prsr.parse("* * * JaN mon-FRI");

        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(month instanceof SingleValueRule);
        assertEquals(Calendar.JANUARY, ((SingleValueRule) month).getValue());

        assertTrue(dow instanceof RangeRule);
        assertEquals(Calendar.MONDAY, ((RangeRule) dow).getMin());
        assertEquals(Calendar.FRIDAY, ((RangeRule) dow).getMax());
    }

    /** Tests the parsing of the special expression {@code @reboot}. */
    @Test
    public void testAtReboot() {
        assertTrue(prsr.isValid("@reboot"));
        try {
            prsr.parse("@reboot");
            fail();
        } catch (UnsupportedCronException e) {
            assertTrue(e.isValid());
            assertEquals("@reboot", e.getCron());
        }
    }

    /** Tests the parsing of the special expression {@code @yearly}. */
    @Test
    public void testAtYearly() throws UnsupportedCronException {
        testAtYearly("@yearly");
    }

    /** Tests the parsing of the special expression {@code @annually}. */
    @Test
    public void testAtAnnually() throws UnsupportedCronException {
        testAtYearly("@annually");
    }

    private void testAtYearly(String special) throws UnsupportedCronException {
        assertTrue(prsr.isValid(special));
        CronExpression expr = prsr.parse(special);

        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dom instanceof SingleValueRule);
        assertEquals(1, ((SingleValueRule) dom).getValue());

        assertTrue(month instanceof SingleValueRule);
        assertEquals(Calendar.JANUARY, ((SingleValueRule) month).getValue());

        assertTrue(dow instanceof AnyValueRule);
    }

    /** Tests the parsing of the special expression {@code @monthly}. */
    @Test
    public void testAtMonthly() throws UnsupportedCronException {
        assertTrue(prsr.isValid("@monthly"));
        CronExpression expr = prsr.parse("@monthly");

        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dom instanceof SingleValueRule);
        assertEquals(1, ((SingleValueRule) dom).getValue());

        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }

    /** Tests the parsing of the special expression {@code @weekly}. */
    @Test
    public void testAtWeekly() throws UnsupportedCronException {
        assertTrue(prsr.isValid("@weekly"));
        CronExpression expr = prsr.parse("@weekly");

        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dow instanceof SingleValueRule);
        assertEquals(Calendar.SUNDAY, ((SingleValueRule) dow).getValue());

        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
    }

    /** Tests the parsing of the special expression {@code @daily}. */
    @Test
    public void testAtDaily() throws UnsupportedCronException {
        testAtDaily("@daily");
    }

    /** Tests the parsing of the special expression {@code @midnight}. */
    @Test
    public void testAtMidnight() throws UnsupportedCronException {
        testAtDaily("@midnight");
    }

    private void testAtDaily(String special) throws UnsupportedCronException {
        assertTrue(prsr.isValid(special));
        CronExpression expr = prsr.parse(special);

        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }

    /** Tests the parsing of the special expression {@code @hourly}. */
    @Test
    public void testAtHourly() throws UnsupportedCronException {
        assertTrue(prsr.isValid("@hourly"));
        CronExpression expr = prsr.parse("@hourly");

        CronRule minute = expr.get(Field.MINUTE);
        CronRule hour = expr.get(Field.HOUR);
        CronRule dom = expr.get(Field.DAY_OF_MONTH);
        CronRule month = expr.get(Field.MONTH);
        CronRule dow = expr.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof AnyValueRule);
        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }
}
