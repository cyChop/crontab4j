package org.keyboardplaying.cron.expression;

import org.keyboardplaying.cron.expression.rule.CronRule;

/**
 * The object representation of a CRON expression.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class CronExpression {

    public static enum Field {
        SECOND, MINUTE, HOUR, DAY_OF_MONTH, MONTH, DAY_OF_WEEK, YEAR;
    }

    public static enum DayConstraint {
        NONE, MONTH, WEEK, BOTH_AND, BOTH_OR;
    }

    private CronRule[] rules;
    private DayConstraint dayConstraint;

    private CronExpression(CronRule[] rules, DayConstraint constraint) {
        this.rules = rules;
        this.dayConstraint = constraint;
    }

    public CronRule get(Field field) {
        return rules[field.ordinal()];
    }

    public DayConstraint getDayConstraint() {
        return dayConstraint;
    }

    public static final class Builder {

        private CronRule[] rules = new CronRule[Field.values().length];
        private DayConstraint dayConstraint;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder set(Field field, CronRule rule) {
            rules[field.ordinal()] = rule;
            return this;
        }

        public Builder set(DayConstraint dayConstraint) {
            this.dayConstraint = dayConstraint;
            return this;
        }

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
