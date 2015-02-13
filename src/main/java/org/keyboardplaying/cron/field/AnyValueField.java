package org.keyboardplaying.cron.field;

/**
 * A representation for fields allowing any value ({@code *}).
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class AnyValueField implements CronField {

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.field.CronField.allows(int)
     */
    public boolean allows(int value) {
        return true;
    }
}
