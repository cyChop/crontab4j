package org.keyboardplaying.cron.parser;

import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;

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
}
