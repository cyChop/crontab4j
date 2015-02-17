package org.keyboardplaying.cron.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// XXX Javadoc
public class UnixCronParserTest {

    CronSyntacticParser prsr = new UnixCronParser();

    @Test
    public void testValidate() {
        assertTrue(prsr.isValid("* * * * *"));
        assertTrue(prsr.isValid("0 0 */2 1-7/2,8-12/2 1-5"));
        assertFalse(prsr.isValid("* * * *"));
        assertFalse(prsr.isValid(null));
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() throws InvalidCronException {
        prsr.parse(null);
    }

    @Test(expected = InvalidCronException.class)
    public void testParseInvalid() throws InvalidCronException {
        prsr.parse("* * * *");
    }

    @Test
    public void testParse() throws InvalidCronException {
        CronExpression expr = prsr.parse("0 * 1-15/2,*/3 1/2 1-5");

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
        assertTrue(month instanceof SingleValueRule);
        assertEquals(1, ((SingleValueRule) month).getValue());
        assertTrue(dow instanceof RangeRule);
        assertEquals(Calendar.MONDAY, ((RangeRule) dow).getMin());
        assertEquals(Calendar.FRIDAY, ((RangeRule) dow).getMax());
        assertTrue(year instanceof AnyValueRule);
    }
}
