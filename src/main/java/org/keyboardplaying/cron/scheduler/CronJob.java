package org.keyboardplaying.cron.scheduler;

import java.util.Objects;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class CronJob {

    private Runnable job;
    private String cron;

    /**
     * Creates a new job.
     *
     * @param job
     *            the job to be run
     * @param cron
     *            the CRON expression to trigger this job
     * @throws NullPointerException
     *            if {@code job} is {@code null}
     */
    public CronJob(Runnable job, String cron) {
        Objects.requireNonNull(job);
        Objects.requireNonNull(cron);
        this.job = job;
        this.cron = cron;
    }

    public Runnable getJob() {
        return job;
    }

    public String getCron() {
        return cron;
    }
}
