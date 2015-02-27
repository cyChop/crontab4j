package org.keyboardplaying.cron.syntax.unix;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.keyboardplaying.cron.syntax.unix.CronParser.*;
import org.junit.Test;

public class CronParserTest {

    @Test
    public void test() {
        CronParser parser = new CronParser(new BufferedTokenStream(new CronLexer(new ANTLRInputStream("* * * * *"))));
        CronContext cron = parser.cron();
        System.out.println(cron);
        System.out.println(cron.minuteSegment().minuteRule());
    }
}
