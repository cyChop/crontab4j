package org.keyboardplaying.cron.field;

/**
 * A representation for fields allowing only one value (e.g. {@code 42}).
 * <p/>
 * Only the value provided at construction will be allowed.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class SingleValueField implements CronField {

    private int allowed;

    /**
     * Creates a new instance.
     *
     * @param value
     *            the single authorized value for this field
     */
    public SingleValueField(int value) {
        this.allowed = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.field.CronField.allows(int)
     */
    public boolean allows(int value) {
        return value == allowed;
    }
}
