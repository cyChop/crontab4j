package org.keyboardplaying.cron.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;
import org.keyboardplaying.cron.parser.adapter.RangeAdapter;

/**
 * Parent for syntax-dependent parsers.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public interface CronSyntacticParser {

    /**
     * Tests whether the supplied CRON expression is valid.
     *
     * @param cron
     *            the CRON expression to test
     * @return {@code true} if the expression is valid, {@code false} otherwise
     */
    boolean isValid(String cron);

    /**
     * Parses a CRON expression into an object we can use.
     *
     * @return a parsed {@link CronExpression}
     * @throws NullPointerException
     *             if the expression is {@code null}
     * @throws InvalidCronException
     *             if the expression is invalid
     *
     * @see #isValid(String)
     */
    CronExpression parse(String cron) throws InvalidCronException;

    /**
     * Representation of the rule and parsing specifications of a group.
     * <p/>
     * The "group" in the name of this interface refers to the regex notion of group, as each field
     * of the CRON expression should be isolated as a group before being parsed using the
     * information provided via this interface.
     *
     * @author Cyrille Chopelet (http://keyboardplaying.org)
     */
    static interface CronGroup {

        /**
         * Returns a regular expression to identify the allowed integer values for the rule
         * corresponding to this group.
         *
         * @return the pattern for the range of authorized integer values
         */
        String getRangePattern();

        /**
         * Returns the minimal allowed value for this group.
         *
         * @return the minimal allowed value
         */
        int getMin();

        /**
         * Returns the maximal allowed value for this group.
         *
         * @return the maximal allowed value
         */
        int getMax();

        /**
         * Returns the {@link RangeAdapter} to use when parsing the rule for this group.
         *
         * @return the range adapter
         */
        RangeAdapter getAdapter();
    }

    /**
     * An interface for substitution names (e.g. day or month names) in CRON expressions.
     * <p/>
     * This interface is used when names are to be allowed on a segment of a CRON expression. They
     * basically are represented as an alias-value pair.
     *
     * @author Cyrille Chopelet (http://keyboardplaying.org)
     */
    static interface CronAlias {

        /**
         * Returns the {@link String} representation of this alias.
         *
         * @return the alias
         */
        String getAlias();

        /**
         * Returns the actual value of this alias.
         *
         * @return the values
         */
        int getValue();
    }

    /**
     * Provides utilities for parsing regexes based on the Unix standard.
     * <p/>
     * As the base for the most commonly used CRON formats, Unix CRON standard seemed a good basis.
     * <p/>
     * This utility class provides methods for generating CRON patterns from allowed range and
     * names, and from parsing them back to {@link CronRule}.
     *
     * @author Cyrille Chopelet (http://keyboardplaying)
     */
    static final class CronRegexUtils {

        /** Private constructor to avoid instantiation. */
        private CronRegexUtils() {
        }

        /**
         * Creates a regular expression to match a single, atomic rule.
         * <p/>
         * The generated regex will represent the following idea:
         * {@code *|rangePattern|names(-(rangePattern|names))?(/(rangePattern|names))?}
         *
         * @param rangePattern
         *            a pattern to match the allowed integer values
         * @param names
         *            a list of allowed substitution names for readibility of the CRON expression
         */
        public static String initGroupPattern(String rangePattern, CronAlias[] names) {
            String allowedNames;
            if (names == null || names.length == 0) {
                allowedNames = "";
            } else {
                StringBuilder sb = new StringBuilder();
                for (CronAlias name : names) {
                    sb.append('|').append(name.getAlias());
                }
                allowedNames = sb.toString();
            }
            return "(?:\\*|(" + rangePattern + allowedNames + ")(?:-(" + rangePattern + allowedNames
                    + "))?)(?:/(" + rangePattern + "))?";
        }

        /**
         * Creates a regular expression to match the CRON expression.
         *
         * @param sep
         *            the parameter to be used when allowing multiple rules for a group
         * @param groups
         *            the rule and parsing specifications for each group in the regex; the array
         *            must be ordered the same way the CRON expressions will be
         */
        public static String initCronPattern(String sep, CronGroup[] groups) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (CronGroup group : groups) {
                if (first) {
                    first = false;
                } else {
                    sb.append("\\s+");
                }
                sb.append('(').append(group.getRangePattern());
                if (sep != null) {
                    // make repeatable
                    sb.append("(?:").append(sep).append(group.getRangePattern()).append(")*");
                }
                sb.append(')');
            }
            return sb.toString();
        }

        /**
         * Parses a group to a {@link CronRule}. Only integers and {@code *} are allowed; if names
         * were used in the original expression, they must have been replaced with their integer
         * equivalent before the group is passed to this method.
         *
         * @param grp
         *            the group extracted from the CronExpression
         * @param sep
         *            the separator to be used when allowing multiple rules for a group
         * @param group
         *            the rule and parsing specifications for the group
         *
         * @return the parsed rule
         */
        public static CronRule parseGroup(String grp, String sep, CronGroup group) {
            CronRule result;
            if (sep != null && grp.contains(sep)) {
                List<CronRule> rules = new ArrayList<CronRule>();
                for (String atomic : grp.split(sep)) {
                    rules.add(parseGroup(atomic, null, group));
                }
                result = new MultipleRule(rules);
            } else {
                Matcher matcher = Pattern.compile(group.getRangePattern()).matcher(grp);
                matcher.find();

                String min = matcher.group(1);
                String max = matcher.group(2);
                String step = matcher.group(3);
                if (step == null) {
                    if (min == null) {
                        result = new AnyValueRule();
                    } else if (max == null) {
                        result = group.getAdapter().adapt(
                                new SingleValueRule(Integer.parseInt(min)));
                    } else {
                        result = group.getAdapter().adapt(new RangeRule(Integer.parseInt(min),
                                Integer.parseInt(max)));
                    }
                } else if (min == null) {
                    result = group.getAdapter().adapt(new RepeatRule(group.getMin(), group.getMax(),
                            Integer.parseInt(step)));
                } else if (max == null) {
                    result = group.getAdapter().adapt(new SingleValueRule(Integer.parseInt(min)));
                } else {
                    result = group.getAdapter().adapt(new RepeatRule(Integer.parseInt(min), Integer
                            .parseInt(max), Integer.parseInt(step)));
                }
            }
            return result;
        }
    }
}
