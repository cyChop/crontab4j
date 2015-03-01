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
import org.keyboardplaying.cron.parser.unix.CronLexer;
import org.keyboardplaying.cron.parser.unix.CronParser;
import org.keyboardplaying.cron.parser.unix.CronParser.CronContext;
import org.keyboardplaying.cron.parser.unix.CronParser.MinuteFieldContext;
import org.keyboardplaying.cron.parser.unix.CronParser.RegularContext;
import org.keyboardplaying.cron.parser.unix.CronParser.SpecialExprContext;
import org.keyboardplaying.cron.parser.unix.MinuteRulesLexer;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser.AtomicRuleContext;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser.RangeRuleContext;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser.RepeatRuleContext;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser.RuleSetContext;
import org.keyboardplaying.cron.parser.unix.MinuteRulesParser.SingleValueRuleContext;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public class UnixCronParser implements CronSyntacticParser {

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
            System.out.println("--");
            RegularContext reg = parser.regular();
            return CronExpression.Builder
                    .create()
                    .set(DayConstraint.BOTH_OR)
                    .set(Field.SECOND, new SingleValueRule(0))
                    .set(Field.MINUTE, parseMinuteField(reg.minuteField()))
                    // TODO actual implementation
                    .set(Field.HOUR, new AnyValueRule())
                    .set(Field.DAY_OF_MONTH, new AnyValueRule())
                    .set(Field.MONTH, new AnyValueRule())
                    .set(Field.DAY_OF_WEEK, new AnyValueRule())
                    // .set(Field.HOUR, UnixCronGroup.HOUR.parse(matcher))
                    // .set(Field.DAY_OF_MONTH, UnixCronGroup.DAY_OF_MONTH.parse(matcher))
                    // .set(Field.MONTH, UnixCronGroup.MONTH.parse(matcher))
                    // .set(Field.DAY_OF_WEEK, UnixCronGroup.DAY_OF_WEEK.parse(matcher))
                    .set(Field.YEAR, new AnyValueRule()).build();
        } else {
            return parse(SpecialExpression.valueOf(specialExpr.getText()).getEquivalent());
        }
    }

    private CronRule parseMinuteField(MinuteFieldContext ctx) {
        MinuteRulesParser parser = new MinuteRulesParser(new CommonTokenStream(
                new MinuteRulesLexer(new ANTLRInputStream(ctx.getText()))));
        RuleSetContext ruleSet = parser.ruleSet();
        System.out.println(ruleSet.getText());
        List<AtomicRuleContext> rules = ruleSet.atomicRule();
        if (rules.size() == 1) {
            return parseRule(ruleSet.atomicRule(0));
        } else {
            List<CronRule> result = new ArrayList<CronRule>();
            for (AtomicRuleContext rule : rules) {
                result.add(parseRule(rule));
            }
            return new MultipleRule(result);
        }
    }

    private CronRule parseRule(AtomicRuleContext ctx) {
        if (ctx.anyValueRule() != null) {
            return new AnyValueRule();
        }

        SingleValueRuleContext single = ctx.singleValueRule();
        if (single != null) {
            return new SingleValueRule(Integer.parseInt(single.value().getText()));
        }

        RangeRuleContext range = ctx.rangeRule();
        if (range != null) {
            return new RangeRule(Integer.parseInt(range.min().getText()), Integer.parseInt(range
                    .max().getText()));
        }

        RepeatRuleContext repeat = ctx.repeatRule();
        if (repeat != null) {
            if (repeat.ANY() != null) {
                return new RepeatRule(0, 59, Integer.parseInt(repeat.step().getText()));
            } else {
                return new RepeatRule(Integer.parseInt(repeat.min().getText()),
                        Integer.parseInt(repeat.max().getText()), Integer.parseInt(repeat.step()
                                .getText()));
            }
        }

        throw new UnsupportedCronException(ctx.getText(), false);
    }
}
