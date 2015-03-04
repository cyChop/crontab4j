package org.keyboardplaying.cron.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.keyboardplaying.cron.exception.UnsupportedCronException;
import org.keyboardplaying.cron.expression.CronExpression;
import org.keyboardplaying.cron.expression.CronExpression.DayConstraint;
import org.keyboardplaying.cron.expression.CronExpression.Field;
import org.keyboardplaying.cron.expression.rule.AnyValueRule;
import org.keyboardplaying.cron.expression.rule.CronRule;
import org.keyboardplaying.cron.expression.rule.MultipleRule;
import org.keyboardplaying.cron.expression.rule.RangeRule;
import org.keyboardplaying.cron.expression.rule.RepeatRule;
import org.keyboardplaying.cron.expression.rule.SingleValueRule;
import org.keyboardplaying.cron.parser.adapter.DayOfWeekRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.MonthRangeAdapter;
import org.keyboardplaying.cron.parser.adapter.NoChangeAdapter;
import org.keyboardplaying.cron.parser.adapter.RangeAdapter;
import org.keyboardplaying.cron.parser.unix.CronLexer;
import org.keyboardplaying.cron.parser.unix.CronParser;
import org.keyboardplaying.cron.parser.unix.CronParser.AliasContext;
import org.keyboardplaying.cron.parser.unix.CronParser.AtomicRuleContext;
import org.keyboardplaying.cron.parser.unix.CronParser.CronContext;
import org.keyboardplaying.cron.parser.unix.CronParser.RangeRuleContext;
import org.keyboardplaying.cron.parser.unix.CronParser.RegularContext;
import org.keyboardplaying.cron.parser.unix.CronParser.RepeatRuleContext;
import org.keyboardplaying.cron.parser.unix.CronParser.RulesContext;
import org.keyboardplaying.cron.parser.unix.CronParser.SingleValueRuleContext;
import org.keyboardplaying.cron.parser.unix.CronParser.SpecialExprContext;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class UnixCronParser implements CronSyntacticParser {

    private static final RangeAdapter NO_CHANGE_ADAPTER = new NoChangeAdapter();

    private static final int UNIX_JANUARY = 1;
    private static final int UNIX_SUNDAY = 0;

    private static enum SpecialExpression {
        // reboot
        reboot(null) {
            @Override
            public String getEquivalent() {
                throw new UnsupportedCronException("@reboot", true);
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

        public String getEquivalent() {
            return equivalent;
        }
    }

    private static enum UnixCronField {
        // minutes
        MINUTE(0, 59),
        // hours
        HOUR(0, 23),
        // day of month
        DAY_OF_MONTH(1, 31),
        // month
        MONTH(1, 12, MonthAlias.values(), new MonthRangeAdapter(UNIX_JANUARY)) {
            @Override
            public CronAlias[] getAliases() {
                return MonthAlias.values();
            }
        },
        // day of week
        DAY_OF_WEEK(0, 7, DayOfWeekAlias.values(), new DayOfWeekRangeAdapter(UNIX_SUNDAY));

        private int min;
        private int max;
        private RangeAdapter adapter;

        private UnixCronField(int min, int max) {
            this(min, max, null, NO_CHANGE_ADAPTER);
        }

        private UnixCronField(int min, int max, CronAlias[] aliases, RangeAdapter adapter) {
            this.min = min;
            this.max = max;
            this.adapter = adapter;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public RangeAdapter getAdapter() {
            return adapter;
        }

        public CronAlias[] getAliases() {
            return null;
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

    @Override
    public boolean isValid(String cron) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public CronExpression parse(String cron) {
        CronContext parser = new CronParser(new CommonTokenStream(new CronLexer(
                new ANTLRInputStream(Objects.requireNonNull(cron,
                        "CRON expression must not be null"))))).cron();
        SpecialExprContext specialExpr = parser.specialExpr();
        if (specialExpr == null) {
            RegularContext reg = parser.regular();
            return CronExpression.Builder
                    .create()
                    .set(DayConstraint.BOTH_OR)
                    .set(Field.SECOND, new SingleValueRule(0))
                    .set(Field.MINUTE, parseRules(reg.minuteField().rules(), UnixCronField.MINUTE))
                    .set(Field.HOUR, parseRules(reg.hourField().rules(), UnixCronField.HOUR))
                    .set(Field.DAY_OF_MONTH,
                            parseRules(reg.dayOfMonthField().rules(), UnixCronField.DAY_OF_MONTH))
                    .set(Field.MONTH, parseRules(reg.monthField().rules(), UnixCronField.MONTH))
                    .set(Field.DAY_OF_WEEK,
                            parseRules(reg.dayOfWeekField().rules(), UnixCronField.DAY_OF_WEEK))
                    .set(Field.YEAR, new AnyValueRule()).build();
        } else {
            return parse(SpecialExpression.valueOf(specialExpr.getText()).getEquivalent());
        }
    }

    private CronRule parseRules(RulesContext ctx, UnixCronField field) {
        List<AtomicRuleContext> rules = ctx.atomicRule();
        if (rules.size() == 1) {
            return parseRule(ctx.atomicRule(0), field);
        } else {
            List<CronRule> result = new ArrayList<CronRule>();
            for (AtomicRuleContext rule : rules) {
                result.add(parseRule(rule, field));
            }
            return new MultipleRule(result);
        }
    }

    private CronRule parseRule(AtomicRuleContext ctx, UnixCronField field) {
        if (ctx.anyValueRule() != null) {
            return new AnyValueRule();
        }

        SingleValueRuleContext single = ctx.singleValueRule();
        if (single != null) {
            return field.getAdapter().adapt(
                    new SingleValueRule(parseUnit(single.value().alias(), field)));
        }

        RangeRuleContext range = ctx.rangeRule();
        if (range != null) {
            return field.getAdapter().adapt(
                    new RangeRule(parseUnit(range.min().alias(), field), parseUnit(range.max()
                            .alias(), field)));
        }

        RepeatRuleContext repeat = ctx.repeatRule();
        if (repeat != null) {
            if (repeat.ANY() != null) {
                return field.getAdapter().adapt(
                        new RepeatRule(field.getMin(), field.getMax(), Integer.parseInt(repeat
                                .step().getText())));
            } else {
                return field.getAdapter().adapt(
                        new RepeatRule(parseUnit(repeat.min().alias(), field), parseUnit(repeat
                                .max().alias(), field), Integer.parseInt(repeat.step().getText())));
            }
        }

        throw new IllegalArgumentException(ctx.getText() + " cannot be parsed as a rule for field "
                + field.toString());
    }

    private int parseUnit(AliasContext unit, UnixCronField field) {
        if (unit.UNIT() != null) {
            return Integer.parseInt(unit.getText());
        } else {
            CronAlias[] aliases = field.getAliases();
            String text = unit.getText().toUpperCase();
            if (aliases != null) {
                for (CronAlias alias : aliases) {
                    if (alias.getAlias().equals(text)) {
                        return alias.getValue();
                    }
                }
            }
            throw new IllegalArgumentException("Illegal alias <" + unit + "> for field "
                    + field.toString());
        }
    }
}
