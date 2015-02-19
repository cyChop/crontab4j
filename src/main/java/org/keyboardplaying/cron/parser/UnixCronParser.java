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
import org.keyboardplaying.cron.parser.adapter.AtomicRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.DayOfWeekRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.MonthRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.NoChangeAdapter;

/**
 * A parser for Unix CRON expressions.
 * <p/>
 * The rules are implemented based on the crontab documentation.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 *
 * @see http://www.unix.com/man-page/linux/5/crontab/
 */
// TODO Javadoc
// TODO optimization: adapt day constraint according to expression
// FIXME mon-sun (case insensitive)
// FIXME jan-dec (case insensitive)
// FIXME special strings
public class UnixCronParser implements CronSyntacticParser {

    private static final AtomicRangeAdapter NO_CHANGE_ADAPTER = new NoChangeAdapter();
    private static final int UNIX_JANUARY = 1;
    private static final int UNIX_SUNDAY = 0;

    private static enum CronGroup {
        // minutes
        MINUTE("[1-5]?\\d", 0, 59),
        // hours
        HOUR("2[0-3]|1?\\d", 0, 23),
        // day of month
        DAY_OF_MONTH("3[0-1]|[1-2]?\\d", 1, 31),
        // month
        MONTH("1[0-2]|\\d", 1, 12, new MonthRangeAdapter(UNIX_JANUARY)),
        // day of week
        DAY_OF_WEEK("[0-7]", 0, 7, new DayOfWeekRangeAdapter(UNIX_SUNDAY));

        private String pattern;
        private int min;
        private int max;
        private AtomicRangeAdapter adapter;

        private CronGroup(String rangePattern, int min, int max) {
            this(rangePattern, min, max, NO_CHANGE_ADAPTER);
        }

        private CronGroup(String rangePattern, int min, int max, AtomicRangeAdapter adapter) {
            this.pattern = initAtomicPattern(rangePattern);
            this.min = min;
            this.max = max;
            this.adapter = adapter;
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

        public AtomicRangeAdapter getAdapter() {
            return adapter;
        }
    }

    private static final CronRule SECOND = new SingleValueRule(0);
    private static final CronRule YEAR = new AnyValueRule();

    private static final String PATTERN_CRON = initCronPattern(CronGroup.values());
    private static final String PATTERN_REPEAT_SEP = ",";
    private static final int NB_GROUPS_REPEAT = 7;

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
                    .set(Field.MINUTE, parseGroup(getGroup(matcher, i++), CronGroup.MINUTE))
                    .set(Field.HOUR, parseGroup(getGroup(matcher, i++), CronGroup.HOUR))
                    .set(Field.DAY_OF_MONTH,
                            parseGroup(getGroup(matcher, i++), CronGroup.DAY_OF_MONTH))
                    .set(Field.MONTH, parseGroup(getGroup(matcher, i++), CronGroup.MONTH))
                    .set(Field.DAY_OF_WEEK,
                            parseGroup(getGroup(matcher, i++), CronGroup.DAY_OF_WEEK))
                    .set(Field.YEAR, YEAR).build();
        }
    }

    private String getGroup(Matcher matcher, int i) {
        return matcher.group(1 + i * NB_GROUPS_REPEAT);
    }

    private CronRule parseGroup(String expr, CronGroup group) {
        CronRule result;
        if (expr.contains(PATTERN_REPEAT_SEP)) {
            List<CronRule> rules = new ArrayList<CronRule>();
            for (String atomic : expr.split(PATTERN_REPEAT_SEP)) {
                rules.add(parseGroup(atomic, group));
            }
            result = new MultipleRule(rules);
        } else {
            Matcher matcher = Pattern.compile(group.getPattern()).matcher(expr);
            matcher.find();

            String min = matcher.group(1);
            String max = matcher.group(2);
            String step = matcher.group(3);
            if (step == null) {
                if (min == null) {
                    result = new AnyValueRule();
                } else if (max == null) {
                    result = group.getAdapter().adapt(new SingleValueRule(Integer.parseInt(min)));
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
