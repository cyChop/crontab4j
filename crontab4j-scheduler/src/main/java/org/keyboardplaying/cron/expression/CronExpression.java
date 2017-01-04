package org.keyboardplaying.cron.expression;

import org.keyboardplaying.cron.expression.rule.CronRule;

/**
 * The object representation of a CRON expression.
 * <p/>
 * The {@link CronExpression} is an immutable object which must be built using the appropriate {@link Builder}.
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class CronExpression {

    /**
     * This enum lists all fields a CRON expression can have a rule upon.
     *
     * @author Cyrille Chopelet (https://keyboardplaying.org)
     */
    public enum Field {
        /**
         * The second in a minute.
         */
        SECOND, /**
         * The minute in an hour.
         */
        MINUTE, /**
         * The hour of day.
         */
        HOUR, /**
         * The date of day in month.
         */
        DAY_OF_MONTH, /**
         * The month of year.
         */
        MONTH, /**
         * The day of week.
         */
        DAY_OF_WEEK, /**
         * The year.
         */
        YEAR
    }

    /**
     * The type of constraint on the day.
     * <p/>
     * This can depend upon the CRON syntax being used (Quartz has restrictions on constraints on which constraints can
     * be set on both fields).
     *
     * @author Cyrille Chopelet (https://keyboardplaying.org)
     */
    public enum DayConstraint {
        /**
         * No constraint on day whatsoever.
         */
        NONE, /**
         * Only the day of month (date) is constrained.
         */
        MONTH, /**
         * Only the day of week is constrained.
         */
        WEEK, /**
         * Both the day of month and day of week must match their respective constraint.
         */
        BOTH_AND, /**
         * At least one of the day of month and day of week must match their respective constraint.
         */
        BOTH_OR
    }

    private CronRule[] rules;
    private DayConstraint dayConstraint;

    private CronExpression(CronRule[] rules, DayConstraint constraint) {
        this.rules = rules;
        this.dayConstraint = constraint;
    }

    /**
     * Returns the rule for the specified field.
     *
     * @param field the field
     * @return the rule
     */
    public CronRule get(Field field) {
        return rules[field.ordinal()];
    }

    /**
     * Returns the day constraint mode.
     *
     * @return the {@link DayConstraint}
     */
    public DayConstraint getDayConstraint() {
        return dayConstraint;
    }

    /**
     * Utility to build a {@link CronExpression}.
     * <p/>
     * The methods of this object can be chained to make construction more convenient (as when using
     * {@code StringBuilder.append(Object)}).
     *
     * @author Cyrille Chopelet (https://keyboardplaying.org)
     */
    public static final class Builder {

        private CronRule[] rules = new CronRule[Field.values().length];
        private DayConstraint dayConstraint;

        /**
         * Private constructor to control instantiation.
         */
        private Builder() {
        }

        /**
         * Creates a new builder.
         *
         * @return a new builder
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * Sets the rule for the specified {@link Field}.
         * <p/>
         * If a rule was previously set for this field, it will be overwritten.
         *
         * @return a reference to this object
         */
        public Builder set(Field field, CronRule rule) {
            rules[field.ordinal()] = rule;
            return this;
        }

        /**
         * Sets the day constraint mode for the expression to be built.
         *
         * @return a reference to this object
         */
        public Builder set(DayConstraint dayConstraint) {
            this.dayConstraint = dayConstraint;
            return this;
        }

        /**
         * Builds the {@link CronExpression} from the arguments supplied earlier in definition.
         *
         * @return the built immutable {@link CronExpression}
         * @throws IllegalStateException if the rule is missing for a field or if the day constraint mode has not been set
         */
        public CronExpression build() {
            for (CronRule rule : rules) {
                if (rule == null) {
                    throw new IllegalStateException("Some rules have not been set.");
                }
            }
            if (dayConstraint == null) {
                throw new IllegalStateException("The day constraint mode has not been set.");
            }
            return new CronExpression(rules, dayConstraint);
        }
    }
}
