package org.keyboardplaying.cron.field;

/**
 * A representation for fields allowing a range with a repeat step (e.g. {@code 42-1337/2} or
 * {@code *\/5}).
 * <p/>
 * A {@link RepeatField} must always be defined as a range and a repetition step. In case the repeat
 * is defined as {@code *\/5}, the range is the maximal range allowed for the corresponding field.
 * <p/>
 * A value will be allowed only if it meets both following condition:
 * <ul>
 * <li>It is within the range of allowed values.</li>
 * <li>The sum of it and the lower limit of the range is a multiple of the repetition step.</li>
 * </ul>
 */
public class RepeatField extends RangeField {

    private int step;

    /**
     * Creates a new instance.
     *
     * @param min
     *            the lower range limit
     * @param max
     *            the upper range limit
     * @param step
     *            the repetition interval
     */
    public RepeatField(int min, int max, int step) {
        super(min, max);
        this.step = step;
    }

    /**
     * Returns the repetition interval.
     *
     * @return the repetition interval
     */
    protected int getStep() {
        return step;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.field.CronField.allows(int)
     */
    public boolean allows(int value) {
        return super.allows(value) && (getMin() + value) % step == 0;
    }
}
