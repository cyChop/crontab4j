package org.keyboardplaying.cron.syntax.unix;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.keyboardplaying.cron.syntax.unix.CronParser.*;
import org.junit.Test;

public class CronParserTest {

    @Test
    public void test() {
        CronParser parser = new CronParser(new BufferedTokenStream(new CronLexer(new ANTLRInputStream("*,1-2,1-5/5,0 * * * *"))));
        CronContext cron = parser.cron();
        System.out.println(cron);
        for (AtomicRuleContext atomicRule: cron.minuteRules().atomicRule()) {
            if (atomicRule.anyValueRule() != null)
                System.out.println("*");
            else if (atomicRule.singleValueRule() != null)
                System.out.println("N");
            else if (atomicRule.rangeRule() != null)
                System.out.println("N-N");
            else if (atomicRule.repeatRule() != null)
                System.out.println("N-N/N");
            else
                System.out.println("?");
        }
    }
}
