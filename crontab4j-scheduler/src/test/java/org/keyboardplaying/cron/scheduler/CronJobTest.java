package org.keyboardplaying.cron.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link CronJob}.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CronJobTest {

    private static final String CRON_EVRY_MIN = "* * * * *";

    private Runnable job = new Runnable() {
        @Override
        public void run() {
        }
    };

    /** Ensures the constructor throws a {@link NullPointerException} if the job is {@code null}. */
    @Test(expected = NullPointerException.class)
    public void testNullJob() {
        new CronJob(null, CRON_EVRY_MIN);
    }

    /**
     * Ensures the constructor throws a {@link NullPointerException} if the cron is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void testNullCron() {
        new CronJob(job, null);
    }

    /** Tests the getters. */
    @Test
    public void test() {
        CronJob cj = new CronJob(job, CRON_EVRY_MIN);
        assertEquals(job, cj.getJob());
        assertEquals(CRON_EVRY_MIN, cj.getCron());
    }
}
