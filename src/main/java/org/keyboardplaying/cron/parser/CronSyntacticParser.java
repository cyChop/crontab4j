package org.keyboardplaying.cron.parser;

import org.keyboardplaying.cron.exception.InvalidCronException;
import org.keyboardplaying.cron.expression.CronExpression;

/**
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
// TODO Javadoc
public interface CronSyntacticParser {

    boolean isValid(String cron);

    CronExpression parse(String cron) throws InvalidCronException;
}
