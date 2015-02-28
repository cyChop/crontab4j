grammar Cron;
import MinuteRules, HourRules, DayOfMonthRules;

cron:	            minuteRules hourRules dayOfMonthRules monthRules dayOfWeekRules;

monthRules:         monthRule(','monthRule)*;
monthRule:          MONTH | (ANY | MONTH'-'MONTH('/'MONTH)?);
MONTH:              '1'[0-2]|[1-9];

dayOfWeekRules:     dayOfWeekRule(','dayOfWeekRule)*;
dayOfWeekRule:      DAY_OF_WEEK | (ANY | DAY_OF_WEEK'-'DAY_OF_WEEK('/'DAY_OF_WEEK)?);
DAY_OF_WEEK:        [0-7];
