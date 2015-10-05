package org.keyboardplaying.cron.parser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.keyboardplaying.cron.exception.UnsupportedCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;
import org.keyboardplaying.cron.parser.adapter.DayOfWeekRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.MonthRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.NoChangeAdapter;
import org.keyboardplaying.cron.parser.adapter.RangeAdapter;

/**
 * A parser for Unix-like CRON expressions.
 * <p/>
 * The rules are implemented based on the crontab documentation.
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 *
 * @see <a href="http://www.unix.com/man-page/linux/5/crontab/">Unix manual</a>
 */
// TODO optimization: adapt day constraint according to expression
public class UnixCronParser implements CronSyntacticParser {

    private static final RangeAdapter NO_CHANGE_ADAPTER = new NoChangeAdapter();
    private static final int UNIX_JANUARY = 1;
    private static final int UNIX_SUNDAY = 0;

    private static final String SPECIAL_EXP_KEY = "@";

    private static enum SpecialExpression {
        // reboot
        reboot(null) {
            @Override
            public String getEquivalent() {
                throw new UnsupportedCronException(getExpression(), true);
            }
        },
        // yearly
        yearly("0 0 1 1 *"), annually("0 0 1 1 *"),
        // monthly
        monthly("0 0 1 * *"),
        // weekly
        weekly("0 0 * * 0"),
        // daily
        daily("0 0 * * *"), midnight("0 0 * * *"),
        // hourly
        hourly("0 * * * *");

        private String equivalent;

        private SpecialExpression(String equivalent) {
            this.equivalent = equivalent;
        }

        public String getExpression() {
            return SPECIAL_EXP_KEY + name();
        }

        public String getEquivalent() {
            return equivalent;
        }
    }

    private static enum UnixCronGroup implements CronGroup {
        // minutes
        MINUTE("[1-5]?\\d", 0, 59),
        // hours
        HOUR("2[0-3]|1?\\d", 0, 23),
        // day of month
        DAY_OF_MONTH("3[0-1]|[1-2]?\\d", 1, 31),
        // month
        MONTH("1[0-2]|\\d", 1, 12, MonthAlias.values(), new MonthRangeAdapter(UNIX_JANUARY)) {
            @Override
            public CronRule parse(Matcher matcher) {
                return parse(matcher, MonthAlias.values());
            }
        },
        // day of week
        DAY_OF_WEEK("[0-7]", 0, 7, DayOfWeekAlias.values(), new DayOfWeekRangeAdapter(UNIX_SUNDAY)) {
            @Override
            public CronRule parse(Matcher matcher) {
                return parse(matcher, DayOfWeekAlias.values());
            }
        };

        private String pattern;
        private int min;
        private int max;
        private RangeAdapter adapter;

        private UnixCronGroup(String rangePattern, int min, int max) {
            this(rangePattern, min, max, null, NO_CHANGE_ADAPTER);
        }

        private UnixCronGroup(String rangePattern, int min, int max, CronAlias[] aliases, RangeAdapter adapter) {
            this.pattern = CronRegexUtils.initGroupPattern(rangePattern, aliases, false);
            this.min = min;
            this.max = max;
            this.adapter = adapter;
        }

        @Override
        public String getRangePattern() {
            return pattern;
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }

        @Override
        public RangeAdapter getAdapter() {
            return adapter;
        }

        public CronRule parse(Matcher matcher) {
            return CronRegexUtils.parseGroup(matcher.group(NB_GROUPS_BASE + ordinal() * NB_GROUPS_REPEAT),
                    PATTERN_REPEAT_SEP, this);
        }

        protected CronRule parse(Matcher matcher, CronAlias[] aliases) {
            String group = matcher.group(NB_GROUPS_BASE + ordinal() * NB_GROUPS_REPEAT).toUpperCase();
            for (CronAlias alias : aliases) {
                group = group.replaceAll(alias.getAlias(), String.valueOf(alias.getValue()));
            }
            return CronRegexUtils.parseGroup(group, PATTERN_REPEAT_SEP, this);
        }
    }

    private static enum MonthAlias implements CronAlias {
        JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC;

        @Override
        public String getAlias() {
            return name();
        }

        @Override
        public int getValue() {
            return ordinal() + UNIX_JANUARY;
        }
    }

    private static enum DayOfWeekAlias implements CronAlias {
        SUN, MON, TUE, WED, THU, FRI, SAT;

        @Override
        public String getAlias() {
            return name();
        }

        @Override
        public int getValue() {
            return ordinal() + UNIX_SUNDAY;
        }
    }

    private static final CronRule SECOND = new SingleValueRule(0);
    private static final CronRule YEAR = new AnyValueRule();

    private static final String PATTERN_REPEAT_SEP = ",";
    private static final String PATTERN_CRON = initUnixCronPattern();

    private static final int NB_GROUPS_BASE = 1;
    private static final int NB_GROUPS_REPEAT = 7;

    private static String initUnixCronPattern() {
        StringBuilder sb = new StringBuilder();
        // the standard regex
        sb.append(CronRegexUtils.initCronPattern(PATTERN_REPEAT_SEP, UnixCronGroup.values()));
        // the special expressions
        for (SpecialExpression se : SpecialExpression.values()) {
            sb.append('|').append(se.getExpression());
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
    public CronExpression parse(String cron) {
        if (!Objects.requireNonNull(cron).matches(PATTERN_CRON)) {
            throw new UnsupportedCronException(cron, false);
        }

        String toParse = cron.startsWith(SPECIAL_EXP_KEY) ? SpecialExpression.valueOf(cron.substring(1)).getEquivalent()
                : cron;
        Matcher matcher = Pattern.compile(PATTERN_CRON).matcher(toParse);
        matcher.find();

        return CronExpression.Builder.create().set(DayConstraint.BOTH_OR).set(Field.SECOND, SECOND)
                .set(Field.MINUTE, UnixCronGroup.MINUTE.parse(matcher))
                .set(Field.HOUR, UnixCronGroup.HOUR.parse(matcher))
                .set(Field.DAY_OF_MONTH, UnixCronGroup.DAY_OF_MONTH.parse(matcher))
                .set(Field.MONTH, UnixCronGroup.MONTH.parse(matcher))
                .set(Field.DAY_OF_WEEK, UnixCronGroup.DAY_OF_WEEK.parse(matcher)).set(Field.YEAR, YEAR).build();
    }
}
