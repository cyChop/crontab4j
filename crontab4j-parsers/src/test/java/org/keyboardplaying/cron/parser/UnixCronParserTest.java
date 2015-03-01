package org.keyboardplaying.cron.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * Tests {@link UnixCronParser}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class UnixCronParserTest {

    CronSyntacticParser prsr = new UnixCronParser();

    /**
     * Ensures the {@link CronExpression} obtained from the parsing of a complex expression is
     * correct.
     */
    @Test
    public void testParse() {
        // CronExpression cron = prsr.parse("0 * * * *");
        CronExpression cron = prsr.parse("@yearly");

        CronRule minute = cron.get(Field.MINUTE);

        assertTrue(minute instanceof SingleValueRule);
        assertEquals(0, ((SingleValueRule) minute).getValue());
    }
}
