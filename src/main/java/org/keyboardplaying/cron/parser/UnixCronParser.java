package org.keyboardplaying.cron.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 *
 * @see http://www.unix.com/man-page/linux/5/crontab/
 */
// TODO Javadoc
// FIXME mon-sun (case insensitive)
// FIXME jan-dec (case insensitive)
// FIXME special strings
public class UnixCronParser implements CronSyntacticParser {

    private static enum CronGroup {
        MINUTE("[1-5]?\\d", 0, 60), HOUR("2[0-3]|1?\\d", 0, 23), DAY_OF_MONTH("3[0-1]|[1-2]?\\d",
                1, 31), MONTH("1[0-2]|\\d", 1, 12), DAY_OF_WEEK("[0-7]", 0, 7);

        private String pattern;
        private int min;
        private int max;

        private CronGroup(String rangePattern, int min, int max) {
            this.pattern = initAtomicPattern(rangePattern);
            this.min = min;
            this.max = max;
        }

        private static String initAtomicPattern(String rangePattern) {
            return "(?:\\*|(" + rangePattern + ")(?:-(" + rangePattern + "))?)(?:/(" + rangePattern
                    + "))?";
        }

        public String getPattern() {
            return pattern;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    private static final String PATTERN_CRON = initCronPattern(CronGroup.values());
    private static final String PATTERN_REPEAT_SEP = ",";
    private static final int NB_GROUPS_REPEAT = 7;

    private static final CronRule SECOND = new SingleValueRule(0);
    private static final CronRule YEAR = new AnyValueRule();

    private static final int UNIX_SUNDAY = 0;
    private AtomicRuleAdapter dowAdapter = new DayOfWeekRangeAdapter(UNIX_SUNDAY);
    private AtomicRuleAdapter nchAdapter = new NoChangeAdapter();

    private static String initCronPattern(CronGroup[] atomicGroups) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (CronGroup group : atomicGroups) {
            if (first) {
                first = false;
            } else {
                sb.append("\\s+");
            }
            sb.append('(').append(group.getPattern());
            // make repeatable
            sb.append("(?:").append(PATTERN_REPEAT_SEP).append(group.getPattern()).append(")?");
            sb.append(')');
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.parser.CronSyntacticParser#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String cron) {
        return cron != null && cron.matches(PATTERN_CRON);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.parser.CronSyntacticParser#parse(java.lang.String)
     */
    @Override
    public CronExpression parse(String cron) throws InvalidCronException {
        Objects.requireNonNull(cron, "cron expression cannot be null.");

        Matcher matcher = Pattern.compile(PATTERN_CRON).matcher(cron.trim());
        if (!matcher.matches()) {
            throw new InvalidCronException(cron);
        } else {
            int i = 0;
            return CronExpression.Builder
                    .create()
                    .set(DayConstraint.BOTH_OR)
                    .set(Field.SECOND, SECOND)
                    .set(Field.MINUTE,
                            parseGroup(getGroup(matcher, i++), CronGroup.MINUTE, nchAdapter))
                    .set(Field.HOUR, parseGroup(getGroup(matcher, i++), CronGroup.HOUR, nchAdapter))
                    .set(Field.DAY_OF_MONTH,
                            parseGroup(getGroup(matcher, i++), CronGroup.DAY_OF_MONTH, nchAdapter))
                    .set(Field.MONTH,
                            parseGroup(getGroup(matcher, i++), CronGroup.MONTH, nchAdapter))
                    .set(Field.DAY_OF_WEEK,
                            parseGroup(getGroup(matcher, i++), CronGroup.DAY_OF_WEEK, dowAdapter))
                    .set(Field.YEAR, YEAR).build();
        }
    }

    private String getGroup(Matcher matcher, int i) {
        return matcher.group(1 + i * NB_GROUPS_REPEAT);
    }

    private CronRule parseGroup(String expr, CronGroup group, AtomicRuleAdapter adptr) {
        CronRule result;
        if (expr.contains(PATTERN_REPEAT_SEP)) {
            List<CronRule> rules = new ArrayList<CronRule>();
            for (String atomic : expr.split(PATTERN_REPEAT_SEP)) {
                rules.add(parseAtomicGroup(atomic, group, adptr));
            }
            result = new MultipleRule(rules);
        } else {
            result = parseAtomicGroup(expr, group, adptr);
        }
        return result;
    }

    private CronRule parseAtomicGroup(String expr, CronGroup group, AtomicRuleAdapter adptr) {
        CronRule result;
        Matcher matcher = Pattern.compile(group.getPattern()).matcher(expr);
        matcher.find();

        String min = matcher.group(1);
        String max = matcher.group(2);
        String step = matcher.group(3);
        if (step == null) {
            if (min == null) {
                result = new AnyValueRule();
            } else if (max == null) {
                result = adptr.adapt(new SingleValueRule(Integer.parseInt(min)));
            } else {
                result = adptr.adapt(new RangeRule(Integer.parseInt(min), Integer.parseInt(max)));
            }
        } else if (min == null) {
            result = adptr.adapt(new RepeatRule(group.getMin(), group.getMax(), Integer
                    .parseInt(step)));
        } else if (max == null) {
            result = adptr.adapt(new SingleValueRule(Integer.parseInt(min)));
        } else {
            result = adptr.adapt(new RepeatRule(Integer.parseInt(min), Integer.parseInt(max),
                    Integer.parseInt(step)));
        }
        return result;
    }
}
