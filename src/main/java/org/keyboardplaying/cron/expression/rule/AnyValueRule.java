package org.keyboardplaying.cron.expression.rule;

/**
 * A representation for fields allowing any value ({@code *}).
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class AnyValueRule implements CronRule {

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
