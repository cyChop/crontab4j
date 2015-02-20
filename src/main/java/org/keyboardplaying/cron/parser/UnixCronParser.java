package org.keyboardplaying.cron.parser;

// import java.util.ArrayList;
// import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
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
// FIXME special strings
public class UnixCronParser implements CronSyntacticParser {

    private static final AtomicRangeAdapter NO_CHANGE_ADAPTER = new NoChangeAdapter();
    private static final int UNIX_JANUARY = 1;
    private static final int UNIX_SUNDAY = 0;

    private static enum UnixCronGroup implements CronGroup {
        // minutes
        MINUTE("[1-5]?\\d", 0, 59),
        // hours
        HOUR("2[0-3]|1?\\d", 0, 23),
        // day of month
        DAY_OF_MONTH("3[0-1]|[1-2]?\\d", 1, 31),
        // month
        MONTH("1[0-2]|\\d", 1, 12, MonthName.values(), new MonthRangeAdapter(UNIX_JANUARY)) {
            @Override
            public CronRule parse(Matcher matcher) {
                return parse(matcher, MonthName.values());
            }
        },
        // day of week
        DAY_OF_WEEK("[0-7]", 0, 7, DayOfWeekName.values(), new DayOfWeekRangeAdapter(UNIX_SUNDAY)) {
            @Override
            public CronRule parse(Matcher matcher) {
                return parse(matcher, DayOfWeekName.values());
            }
        };

        private String pattern;
        private int min;
        private int max;
        private AtomicRangeAdapter adapter;

        private UnixCronGroup(String rangePattern, int min, int max) {
            this(rangePattern, min, max, null, NO_CHANGE_ADAPTER);
        }

        private UnixCronGroup(String rangePattern, int min, int max, CronElementName[] names,
                AtomicRangeAdapter adapter) {
            this.pattern = CronRegexUtils.initGroupPattern(rangePattern, names);
            this.min = min;
            this.max = max;
            this.adapter = adapter;
        }

        public String getRangePattern() {
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

        public CronRule parse(Matcher matcher) {
            return CronRegexUtils.parseGroup(matcher.group(1 + ordinal() * NB_GROUPS_REPEAT),
                    PATTERN_REPEAT_SEP, this);
        }

        protected CronRule parse(Matcher matcher, CronElementName[] names) {
            String group = matcher.group(1 + ordinal() * NB_GROUPS_REPEAT).toUpperCase();
            for (CronElementName name : names) {
                group = group.replaceAll(name.getName(), String.valueOf(name.getReplacement()));
            }
            return CronRegexUtils.parseGroup(group, PATTERN_REPEAT_SEP, this);
        }
    }

    private static enum MonthName implements CronElementName {
        JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC;

        public String getName() {
            return name();
        }

        public int getReplacement() {
            return ordinal() + UNIX_JANUARY;
        }
    }

    private static enum DayOfWeekName implements CronElementName {
        SUN, MON, TUE, WED, THU, FRI, SAT;

        public String getName() {
            return name();
        }

        public int getReplacement() {
            return ordinal() + UNIX_SUNDAY;
        }
    }

    private static final CronRule SECOND = new SingleValueRule(0);
    private static final CronRule YEAR = new AnyValueRule();

    private static final String PATTERN_REPEAT_SEP = ",";
    private static final String PATTERN_CRON =
            CronRegexUtils.initCronPattern(PATTERN_REPEAT_SEP, UnixCronGroup.values());
    private static final int NB_GROUPS_REPEAT = 7;

    /*
     * (non-Javadoc)
     *
     * @see org.keyboardplaying.cron.parser.CronSyntacticParser#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String cron) {
        // TODO finer validation:
        // - ranges are correct (min-max, not max-min)
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

        Matcher matcher = Pattern.compile(PATTERN_CRON, Pattern.CASE_INSENSITIVE)
                .matcher(cron.trim());
        if (!matcher.matches()) {
            throw new InvalidCronException(cron);
        } else {
            return CronExpression.Builder
                    .create()
                    .set(DayConstraint.BOTH_OR)
                    .set(Field.SECOND, SECOND)
                    .set(Field.MINUTE, UnixCronGroup.MINUTE.parse(matcher))
                    .set(Field.HOUR, UnixCronGroup.HOUR.parse(matcher))
                    .set(Field.DAY_OF_MONTH, UnixCronGroup.DAY_OF_MONTH.parse(matcher))
                    .set(Field.MONTH, UnixCronGroup.MONTH.parse(matcher))
                    .set(Field.DAY_OF_WEEK, UnixCronGroup.DAY_OF_WEEK.parse(matcher))
                    .set(Field.YEAR, YEAR).build();
        }
    }
}
