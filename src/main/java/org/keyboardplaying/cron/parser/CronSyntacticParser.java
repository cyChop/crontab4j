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
import org.keyboardplaying.cron.parser.adapter.AtomicRangeAdapter;

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

    static interface CronGroup {

        String getRangePattern();

        int getMin();

        int getMax();

        AtomicRangeAdapter getAdapter();
    }

    static final class CronRegexUtils {

        /** Private constructor to avoid instantiation. */
        private CronRegexUtils() {
        }

        public static String initGroupPattern(String rangePattern) {
            return "(?:\\*|(" + rangePattern + ")(?:-(" + rangePattern + "))?)(?:/(" + rangePattern
                    + "))?";
        }

        public static String initCronPattern(String sep, CronGroup[] atomicGroups) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (CronGroup group : atomicGroups) {
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
