package org.keyboardplaying.cron.expression.rule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A representation for fields with multiple rules ({@code 0,5,15,30,50}).
 *
 * @author Cyrille Chopelet (https://keyboardplaying.org)
 */
public class MultipleRule implements CronRule {

    private Set<CronRule> rules;

    /**
     * Creates a new instance.
     *
     * @param rules the {@link CronRule} representation for each rule
     */
    public MultipleRule(CronRule... rules) {
        this.rules = new HashSet<>();
        Collections.addAll(this.rules, rules);
    }

    /**
     * Creates a new instance.
     *
     * @param rules the {@link CronRule} representation for each rule
     */
    public MultipleRule(Collection<CronRule> rules) {
        this.rules = new HashSet<>(rules);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.hasMax()
     */
    @Override
    public boolean hasMax() {
        for (CronRule rule : rules) {
            if (!rule.hasMax()) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.expression.rule.CronRule.getMax()
     */
    @Override
    public int getMax() {
        int max = 0;
        for (CronRule rule : rules) {
            if (max < rule.getMax()) {
                max = rule.getMax();
            }
        }
        return max;
    }

    /**
     * Returns {@code true} if at least one of the constituting rules allows the supplied value.
     *
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean allows(int value) {
        for (CronRule rule : rules) {
            if (rule.allows(value)) {
                return true;
            }
        }
        return false;
    }

}
