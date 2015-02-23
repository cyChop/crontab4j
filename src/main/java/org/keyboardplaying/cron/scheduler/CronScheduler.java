package org.keyboardplaying.cron.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.keyboardplaying.cron.exception.UnsupportedCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.parser.CronSyntacticParser;
import org.keyboardplaying.cron.parser.UnixCronParser;
import org.keyboardplaying.cron.predictor.CronPredictor;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class CronScheduler {

    private static CronPredictor predictor = new CronPredictor();
    private static CronSyntacticParser parser;

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
     *            if {@code name} is {@code null}
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
     *            if {@code name} is {@code null}
     */
    public CronScheduler(String threadName, boolean daemon) {
        this.timer = new Timer(threadName);
    }

    public void setParser(CronSyntacticParser parser) {
        this.parser = parser;
    }

    private CronSyntacticParser getParser() {
        if (parser == null) {
            parser = new UnixCronParser();
        }
        return parser;
    }

    private static void scheduleNext(Timer timer, TimerTask task, CronExpression expr) {
        Calendar next = predictor.getNextOccurrence(expr);
        if (next != null) {
            timer.schedule(task, next.getTime());
        }
    }

    public void startJob(CronJob job) throws UnsupportedCronException {
        CronExpression expr = getParser().parse(job.getCron());
        scheduleNext(timer, new TimerTaskAdapter(job.getJob(), timer, expr), expr);
    }

    public void stopAllJobs() {
        timer.cancel();
    }

    private static class TimerTaskAdapter extends TimerTask {

        private Runnable job;
        private Timer timer;
        private CronExpression expr;

        private boolean locked = false;

        public TimerTaskAdapter(Runnable job, Timer timer, CronExpression expr) {
            this.job = job;
            this.timer = timer;
            this.expr = expr;
        }

        @Override
        public void run() {
            // prepare the next trigger
            scheduleNext(timer, new TimerTaskAdapter(job, timer, expr), expr);
            // run the job
            job.run();
        }
    }
}
