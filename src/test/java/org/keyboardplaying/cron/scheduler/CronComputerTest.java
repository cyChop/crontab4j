package org.keyboardplaying.cron.scheduler;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import org.junit.Test;
import org.keyboardplaying.cron.expression.CronExpression.Builder;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests the {@link CronComputer}.
 *
 * @author Cyrille Chopelet
 */
// XXX Javadoc
// TODO test multiple field
public class CronComputerTest {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /** Tests the incrementing of several fields with a Unix-expression such as {@code * * * *}. */
    @Test
    public void testBasicCaseAndIncrements() throws ParseException {
        CronRule any = new AnyValueRule();
        CronComputer cpu = new CronComputer(Builder.create()
                .set(Field.SECOND, new SingleValueRule(0)).set(Field.MINUTE, any)
                .set(Field.HOUR, any).set(Field.DAY_OF_MONTH, any).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, any).set(Field.YEAR, new RangeRule(1970, 2016))
                .set(DayConstraint.NONE).build());

        assertCalEquals("2015-02-05T13:38:00", cpu, "2015-02-05T13:37:00");
        assertCalEquals("2015-02-05T13:37:00", cpu, "2015-02-05T13:36:30");
        assertCalEquals("2015-02-05T17:00:00", cpu, "2015-02-05T16:59:01");
        assertCalEquals("2015-02-06T00:00:00", cpu, "2015-02-05T23:59:02");
        assertCalEquals("2015-03-01T00:00:00", cpu, "2015-02-28T23:59:03");
        assertCalEquals("2015-03-01T00:00:00", cpu, "2015-02-28T23:59:04");
        assertCalEquals("2015-04-01T00:00:00", cpu, "2015-03-31T23:59:05");
        assertCalEquals("2015-05-01T00:00:00", cpu, "2015-04-30T23:59:06");
        assertCalEquals("2016-01-01T00:00:00", cpu, "2015-12-31T23:59:07");
        assertCalEquals(null, cpu, "2016-12-31T23:59:08");
    }

    @Test
    public void testTimeUpperIncrement() throws ParseException {
        CronRule any = new AnyValueRule();
        CronComputer cpu = new CronComputer(Builder.create()
                .set(Field.SECOND, new SingleValueRule(0))
                .set(Field.MINUTE, new SingleValueRule(37))
                .set(Field.HOUR, new SingleValueRule(13)).set(Field.DAY_OF_MONTH, any)
                .set(Field.MONTH, any).set(Field.DAY_OF_WEEK, any)
                .set(Field.YEAR, new RangeRule(1970, 2015)).set(DayConstraint.NONE).build());

        assertCalEquals("2015-02-05T13:37:00", cpu, "2015-02-05T10:40:00");
        assertCalEquals("2015-02-05T13:37:00", cpu, "2015-02-05T13:30:00");
        assertCalEquals("2015-02-06T13:37:00", cpu, "2015-02-05T13:42:00");
        assertCalEquals(null, cpu, "2015-12-31T13:42:00");
    }

    @Test
    public void testDayOfMonthConstraints() throws ParseException {
        CronRule any = new AnyValueRule();
        CronRule zero = new SingleValueRule(0);
        CronComputer cpu = new CronComputer(Builder.create().set(Field.SECOND, zero)
                .set(Field.MINUTE, zero).set(Field.HOUR, zero)
                .set(Field.DAY_OF_MONTH, new RepeatRule(1, 31, 10)).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, any).set(Field.YEAR, new RangeRule(1970, 2015))
                .set(DayConstraint.MONTH).build());

        assertCalEquals("2015-02-01T00:00:00", cpu, "2015-01-31T16:42:30");
        assertCalEquals("2015-02-11T00:00:00", cpu, "2015-02-01T00:00:00");
        assertCalEquals("2015-02-21T00:00:00", cpu, "2015-02-11T00:00:00");
        assertCalEquals("2015-03-01T00:00:00", cpu, "2015-02-21T00:00:00");
        assertCalEquals(null, cpu, "2015-12-31T00:00:00");
    }

    @Test
    public void testDayOfWeekConstraints() throws ParseException {
        CronRule any = new AnyValueRule();
        CronRule zero = new SingleValueRule(0);
        CronComputer cpu;

        cpu = new CronComputer(Builder.create().set(Field.SECOND, zero).set(Field.MINUTE, zero)
                .set(Field.HOUR, zero).set(Field.DAY_OF_MONTH, any).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, new RangeRule(Calendar.MONDAY, Calendar.FRIDAY))
                .set(Field.YEAR, new RangeRule(1970, 2015)).set(DayConstraint.WEEK).build());

        assertCalEquals("2015-02-02T00:00:00", cpu, "2015-01-31T16:42:30");
        assertCalEquals("2015-02-03T00:00:00", cpu, "2015-02-02T00:00:00");
        assertCalEquals("2015-02-16T00:00:00", cpu, "2015-02-13T00:00:00");
        assertCalEquals("2015-03-02T00:00:00", cpu, "2015-02-28T00:00:00");
        assertCalEquals(null, cpu, "2015-12-31T00:00:00");

        cpu = new CronComputer(Builder.create().set(Field.SECOND, zero).set(Field.MINUTE, zero)
                .set(Field.HOUR, zero).set(Field.DAY_OF_MONTH, any)
                .set(Field.MONTH, new RepeatRule(Calendar.JANUARY, Calendar.DECEMBER, 2))
                .set(Field.DAY_OF_WEEK, new RangeRule(Calendar.MONDAY, Calendar.FRIDAY))
                .set(Field.YEAR, new RangeRule(1970, 2015)).set(DayConstraint.WEEK).build());

        assertCalEquals("2015-03-02T00:00:00", cpu, "2015-01-31T00:00:00");
    }

    @Test
    public void testBothOrDayConstraints() throws ParseException {
        CronRule any = new AnyValueRule();
        CronRule zero = new SingleValueRule(0);
        CronComputer cpu = new CronComputer(Builder.create().set(Field.SECOND, zero)
                .set(Field.MINUTE, zero).set(Field.HOUR, zero)
                .set(Field.DAY_OF_MONTH, new RepeatRule(1, 31, 10)).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, new RangeRule(Calendar.MONDAY, Calendar.FRIDAY))
                .set(Field.YEAR, new RangeRule(1970, 2015)).set(DayConstraint.BOTH_OR).build());

        assertCalEquals("2015-02-01T00:00:00", cpu, "2015-01-31T16:42:30");
        assertCalEquals("2015-02-02T00:00:00", cpu, "2015-02-01T00:00:00");
        assertCalEquals("2015-02-21T00:00:00", cpu, "2015-02-20T13:37:00");
        assertCalEquals("2015-02-23T00:00:00", cpu, "2015-02-21T00:00:00");
        assertCalEquals(null, cpu, "2015-12-31T00:00:00");
    }

    @Test
    public void testBothAndDayConstraints() throws ParseException {
        CronRule any = new AnyValueRule();
        CronRule zero = new SingleValueRule(0);
        CronComputer cpu = new CronComputer(Builder.create().set(Field.SECOND, zero)
                .set(Field.MINUTE, zero).set(Field.HOUR, zero)
                .set(Field.DAY_OF_MONTH, new RepeatRule(1, 31, 10)).set(Field.MONTH, any)
                .set(Field.DAY_OF_WEEK, new RangeRule(Calendar.MONDAY, Calendar.FRIDAY))
                .set(Field.YEAR, new RangeRule(1970, 2015)).set(DayConstraint.BOTH_AND).build());

        assertCalEquals("2015-02-11T00:00:00", cpu, "2015-01-31T16:42:30");
        assertCalEquals("2015-02-11T00:00:00", cpu, "2015-02-01T00:00:00");
        assertCalEquals("2015-03-11T00:00:00", cpu, "2015-02-20T13:37:00");
        assertCalEquals("2015-03-31T00:00:00", cpu, "2015-03-11T00:00:00");
        assertCalEquals(null, cpu, "2015-12-31T00:00:00");
    }

    @Test
    public void testLeapYears() throws ParseException {
        CronRule any = new AnyValueRule();
        CronRule zero = new SingleValueRule(0);
        CronComputer cpu;

        // 29/02 in a range with no leap years
        cpu = new CronComputer(Builder.create().set(Field.SECOND, zero).set(Field.MINUTE, zero)
                .set(Field.HOUR, zero).set(Field.DAY_OF_MONTH, new SingleValueRule(29))
                .set(Field.MONTH, new SingleValueRule(Calendar.FEBRUARY))
                .set(Field.DAY_OF_WEEK, any).set(Field.YEAR, new RangeRule(2013, 2015))
                .set(DayConstraint.MONTH).build());

        assertCalEquals(null, cpu, "2012-03-01T00:00:00");

        // 29/02 in a range with leap years
        cpu = new CronComputer(Builder.create().set(Field.SECOND, zero).set(Field.MINUTE, zero)
                .set(Field.HOUR, zero).set(Field.DAY_OF_MONTH, new SingleValueRule(29))
                .set(Field.MONTH, new SingleValueRule(Calendar.FEBRUARY))
                .set(Field.DAY_OF_WEEK, any).set(Field.YEAR, new RangeRule(2012, 2050))
                .set(DayConstraint.MONTH).build());

        assertCalEquals("2016-02-29T00:00:00", cpu, "2012-03-01T00:00:00");
    }

    private void assertCalEquals(String expected, CronComputer cpu, String argument)
            throws ParseException {
        Calendar expectation = expected == null ? null : parse(expected);
        Calendar actual = cpu.getNextOccurrence(parse(argument));
        if (!Objects.equals(expectation, actual)) {
            fail("expected " + format(expectation) + " but was " + format(actual));
        }
    }

    private String format(Calendar cal) {
        return cal == null ? "null" : "<" + df.format(cal.getTime()) + ">";
    }

    private Calendar parse(String source) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(source));
        return cal;
    }
}
