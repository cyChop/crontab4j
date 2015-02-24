package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.parser.CronSyntacticParser;
import org.keyboardplaying.cron.parser.UnixCronParser;
import org.keyboardplaying.cron.predictor.CronPredictor;

/**
 * The CRON scheduler.
 * <p/>
 * It is expected to be used as a singleton. When used inside a Spring context, you should specify a
 * parser ({@link #setParser(org.keyboardplaying.cron.parser.CronSyntacticParser}) and a list of
 * jobs ({@link #setJobs(java.util.List)}). All jobs set this way will be started automatically.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Remove Timer and TimerTask, use a custom mechanism based on Thread instead
public class CronScheduler {

    private static CronPredictor predictor = new CronPredictor();

    private CronSyntacticParser parser;
    private Timer timer;

    /** Creates a scheduler whose associated thread will run as a daemon. */
    public CronScheduler() {
        this(true);
    }

    /**
     * Creates a new scheduler whose associated thread may be specified to run as a daemon.
     *
     * @param daemon
     *            {@code true} if the associated thread should run as a daemon, {@code false}
     *            otherwise
     */
    public CronScheduler(boolean daemon) {
        this.timer = new Timer(daemon);
    }

    /**
     * Creates a new scheduler whose associated thread has the specified name.
     *
     * @param threadName
     *            the name of the associated thread
     * @throws NullPointerException
     *             if {@code name} is {@code null}
     */
    public CronScheduler(String threadName) {
        this.timer = new Timer(threadName);
    }

    /**
     * Creates a new scheduler whose associated thread has the specified name, may be specified to
     * run as a daemon.
     *
     * @param threadName
     *            the name of the associated thread
     * @param daemon
     *            {@code true} if the associated thread should run as a daemon, {@code false}
     *            otherwise
     * @throws NullPointerException
     *             if {@code name} is {@code null}
     */
    public CronScheduler(String threadName, boolean daemon) {
        this.timer = new Timer(threadName);
    }

    /**
     * Sets the parser to use for the jobs' CRON expressions.
     * <p/>
     * If not explicitly set, a {@link UnixCronParser} will be used.
     *
     * @param parser
     *            a syntactic CRON parser
     */
    public void setParser(CronSyntacticParser parser) {
        this.parser = parser;
    }

    /**
     * Returns the CRON parser. Defaults to a {@link UnixCronParser}.
     *
     * @return the CRON parser
     */
    private CronSyntacticParser getParser() {
        if (parser == null) {
            parser = new UnixCronParser();
        }
        return parser;
    }

    /**
     * Schedules the next occurrence of a task based on a CRON expression.
     *
     * @param timer
     *            the timer the task should be scheduled at
     * @param task
     *            the task to schedule
     * @param cron
     *            the CRON expression used for scheduling
     */
    // static so it can be used inside a static inner class
    private static void scheduleNext(Timer timer, TimerTask task, CronExpression cron) {
        Calendar next = predictor.getNextOccurrence(cron);
        if (next != null) {
            timer.schedule(task, next.getTime());
        }
    }

    /**
     * Schedules the specified job for execution.
     * <p/>
     * The job will be triggered every time the current time matches its CRON.
     *
     * @param job
     *            the job to schedule
     * @param cron
     *            the CRON trigger
     */
    public void scheduleJob(Runnable job, String cron) {
        CronExpression parsed = getParser().parse(cron);
        scheduleNext(timer, new TimerTaskWrapper(job, timer, parsed), parsed);
    }

    /**
     * Schedules the specified job for execution.
     * <p/>
     * The job will be triggered every time the current time matches its CRON.
     *
     * @param job
     *            the job to schedule
     */
    public void scheduleJob(CronJob job) {
        scheduleJob(job.getJob(), job.getCron());
    }

    /**
     * Utility to schedule several jobs at one time.
     * <p/>
     * This method is called {@code set} so that it can be used from a Spring context, for instance.
     *
     * @param jobs
     *            the jobs to schedule
     *
     * @see #scheduleJob(org.keyboardplaying.cron.scheduler.CronJob)
     */
    // Spring utility
    public void setJobs(Collection<CronJob> jobs) {
        for (CronJob job : jobs) {
            scheduleJob(job);
        }
    }

    /**
     * Terminates this scheduler, discarding any currently scheduled tasks. Does not interfere with
     * a currently executing task (if it exists). Once a scheduler has been terminated, its
     * execution thread terminates gracefully, and no more tasks may be scheduled on it.
     * <p/>
     * Note that calling this method from within the run method of a timer task that was invoked by
     * this timer absolutely guarantees that the ongoing task execution is the last task execution
     * that will ever be performed by this timer.
     * <p/>
     * This method may be called repeatedly; the second and subsequent calls have no effect.
     */
    public void terminate() {
        timer.cancel();
        // ensure all references are released
        timer.purge();
    }

    /**
     * A wrapper to execute {@link CronJob} instances in scheduler.
     *
     * @author Cyrille Chopelet (http://keyboardplaying.org)
     */
    // Keep this a nested class as it is expected to disappear in a future version.
    private static class TimerTaskWrapper extends TimerTask {

        private Runnable job;
        private Timer timer;
        private CronExpression cron;

        /**
         * Creates a new wrapper.
         *
         * @param job
         *            the job wrapped in the new instance
         * @param timer
         *            the timer used to
         */
        public TimerTaskWrapper(Runnable job, Timer timer, CronExpression cron) {
            this.job = job;
            this.timer = timer;
            this.cron = cron;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask.run()
         */
        @Override
        public void run() {
            // prepare the next trigger
            scheduleNext(timer, new TimerTaskWrapper(job, timer, cron), cron);
            // run the job
            job.run();
        }
    }
}
