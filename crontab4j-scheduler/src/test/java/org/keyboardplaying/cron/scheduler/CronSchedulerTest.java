package org.keyboardplaying.cron.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.parser.CronSyntacticParser;

/**
 * Tests {@link CronScheduler}.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class CronSchedulerTest {

    private Runnable job = new Runnable() {
        @Override
        public void run() {
            latch.countDown();
        }
    };

    private CronScheduler schd;
    private CountDownLatch latch;

    /* Initializes the CronScheduler. */ {
        schd = new CronScheduler();
        // Use a mock CRON parser to trigger a CRON everyy second.
        schd.setParser(new CronSyntacticParser() {

            @Override
            public boolean isValid(String cron) {
                return true;
            }

            @Override
            public CronExpression parse(String cron) {
                final CronRule any = new AnyValueRule();
                return CronExpression.Builder.create().set(DayConstraint.NONE).set(Field.SECOND, any)
                        .set(Field.MINUTE, any).set(Field.HOUR, any).set(Field.DAY_OF_MONTH, any).set(Field.MONTH, any)
                        .set(Field.DAY_OF_WEEK, any).set(Field.YEAR, any).build();
            }
        });
    }

    /**
     * Tests the execution and recurrence of a job with a CronScheduler (2 executions).
     */
    @Test(timeout = 3500)
    public void testExecution() throws InterruptedException {
        latch = new CountDownLatch(2);
        schd.scheduleJob(job, "* * * * * *");
        latch.await(3000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());
    }

    /**
     * Tests the execution and recurrence of a job with a CronScheduler (2 executions).
     */
    @Test(timeout = 3500)
    public void testExecutionList() throws InterruptedException {
        List<CronJob> jobs = new ArrayList<>();
        jobs.add(new CronJob(job, "* * * * *"));

        latch = new CountDownLatch(2);
        schd.setJobs(jobs);
        latch.await(3000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());
    }

    /**
     * Ensures that {@link CronScheduler#stopAllJobs} immediately stops all awaiting executions.
     */
    @Test(timeout = 500)
    public void testTerminate() throws InterruptedException {
        latch = new CountDownLatch(2);

        schd.scheduleJob(new CronJob(job, "* * * * * *"));
        schd.terminate();
        assertEquals(2, latch.getCount());
    }
}
