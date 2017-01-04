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
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class UnixCronParserTest {

    private CronSyntacticParser prsr = new UnixCronParser();

    /**
     * Tests {@link UnixCronParser#isValid(java.lang.String)}.
     */
    @Test
    public void testValidate() {
        assertTrue(prsr.isValid("* * * * *"));
        assertTrue(prsr.isValid("0 0 */2 1-7/2,8-12/2 1-5"));
        assertFalse(prsr.isValid("* * * *"));
        assertFalse(prsr.isValid(null));
    }

    /**
     * Ensures the parser fails with a {@link NullPointerException} if the supplied expression is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void testParseNull() {
        prsr.parse(null);
    }

    /**
     * Ensures the parser fails with a {@link UnsupportedCronException} if the supplied expression does not match the
     * validation regex.
     */
    @Test
    public void testParseInvalid() {
        try {
            prsr.parse("* * * */mon *");
            fail();
        } catch (UnsupportedCronException e) {
            assertFalse(e.isValid());
            assertEquals("* * * */mon *", e.getCron());
        }
    }

    /**
     * Ensures the {@link CronExpression} obtained from the parsing of a complex expression is correct.
     */
    @Test
    public void testParse() {
        CronExpression cron = prsr.parse("0 * 1-15/2,*/3,31 1/2 1-5");

        CronRule second = cron.get(Field.SECOND);
        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);
        CronRule year = cron.get(Field.YEAR);

        assertEquals(DayConstraint.BOTH_OR, cron.getDayConstraint());

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
        assertEquals(Calendar.FRIDAY, dow.getMax());

        assertTrue(year instanceof AnyValueRule);
    }

    /**
     * Ensures the parsing of {@link CronExpression} obtained from the parsing of an expression using names for months
     * and days of week is correct.
     */
    @Test
    public void testParseWithNames() {
        // also test names are case insensitive
        assertTrue(prsr.isValid("* * * JaN mon-FRI"));
        CronExpression cron = prsr.parse("* * * JaN mon-FRI");

        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

        assertTrue(month instanceof SingleValueRule);
        assertEquals(Calendar.JANUARY, ((SingleValueRule) month).getValue());

        assertTrue(dow instanceof RangeRule);
        assertEquals(Calendar.MONDAY, ((RangeRule) dow).getMin());
        assertEquals(Calendar.FRIDAY, dow.getMax());
    }

    /**
     * Tests the parsing of the special expression {@code @reboot}.
     */
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

    /**
     * Tests the parsing of the special expression {@code @yearly}.
     */
    @Test
    public void testAtYearly() {
        testAtYearly("@yearly");
    }

    /**
     * Tests the parsing of the special expression {@code @annually}.
     */
    @Test
    public void testAtAnnually() {
        testAtYearly("@annually");
    }

    private void testAtYearly(String special) {
        assertTrue(prsr.isValid(special));
        CronExpression cron = prsr.parse(special);

        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

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

    /**
     * Tests the parsing of the special expression {@code @monthly}.
     */
    @Test
    public void testAtMonthly() {
        assertTrue(prsr.isValid("@monthly"));
        CronExpression cron = prsr.parse("@monthly");

        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dom instanceof SingleValueRule);
        assertEquals(1, ((SingleValueRule) dom).getValue());

        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }

    /**
     * Tests the parsing of the special expression {@code @weekly}.
     */
    @Test
    public void testAtWeekly() {
        assertTrue(prsr.isValid("@weekly"));
        CronExpression cron = prsr.parse("@weekly");

        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dow instanceof SingleValueRule);
        assertEquals(Calendar.SUNDAY, ((SingleValueRule) dow).getValue());

        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
    }

    /**
     * Tests the parsing of the special expression {@code @daily}.
     */
    @Test
    public void testAtDaily() {
        testAtDaily("@daily");
    }

    /**
     * Tests the parsing of the special expression {@code @midnight}.
     */
    @Test
    public void testAtMidnight() {
        testAtDaily("@midnight");
    }

    private void testAtDaily(String special) {
        assertTrue(prsr.isValid(special));
        CronExpression cron = prsr.parse(special);

        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) hour).getValue());

        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }

    /**
     * Tests the parsing of the special expression {@code @hourly}.
     */
    @Test
    public void testAtHourly() {
        assertTrue(prsr.isValid("@hourly"));
        CronExpression cron = prsr.parse("@hourly");

        CronRule minute = cron.get(Field.MINUTE);
        CronRule hour = cron.get(Field.HOUR);
        CronRule dom = cron.get(Field.DAY_OF_MONTH);
        CronRule month = cron.get(Field.MONTH);
        CronRule dow = cron.get(Field.DAY_OF_WEEK);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());

        assertTrue(hour instanceof AnyValueRule);
        assertTrue(dom instanceof AnyValueRule);
        assertTrue(month instanceof AnyValueRule);
        assertTrue(dow instanceof AnyValueRule);
    }
}
