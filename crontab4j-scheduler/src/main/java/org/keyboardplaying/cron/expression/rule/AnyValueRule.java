package org.keyboardplaying.cron.expression.rule;

/**
 * A representation for fields allowing any value ({@code *}).
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class AnyValueRule implements CronRule {

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.hasMax()
     */
    @Override
    public boolean hasMax() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.getMax()
     */
    @Override
    public int getMax() {
        return Integer.MAX_VALUE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.allows(int)
     */
    @Override
    public boolean allows(int value) {
        return true;
    }
}
