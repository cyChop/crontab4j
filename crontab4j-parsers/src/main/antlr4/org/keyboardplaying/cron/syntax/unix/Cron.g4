grammar Cron;
cron:	        minuteSegment hourSegment dayOfMonthSegment monthSegment dayOfWeekSegment;

ANY     : '*';

minuteSegment:  minuteRule(','minuteRule)*;
minuteRule:     MINUTE | (ANY | MINUTE'-'MINUTE('/'MINUTE)?);
MINUTE  : [1-5]?[0-9];

hourSegment:  hourRule(','hourRule)*;
hourRule:     HOUR | (ANY | HOUR'-'HOUR('/'HOUR)?);
HOUR  : [1-5]?[0-9];

dayOfMonthSegment:  dayOfMonthRule(','dayOfMonthRule)*;
dayOfMonthRule:     DAY_OF_MONTH | (ANY | DAY_OF_MONTH'-'DAY_OF_MONTH('/'DAY_OF_MONTH)?);
DAY_OF_MONTH:       [1-3]?[0-9];

monthSegment:  monthRule(','monthRule)*;
monthRule:     MONTH | (ANY | MONTH'-'MONTH('/'MONTH)?);
MONTH  : [1-5]?[0-9];

dayOfWeekSegment:  dayOfWeekRule(','dayOfWeekRule)*;
dayOfWeekRule:     DAY_OF_WEEK | (ANY | DAY_OF_WEEK'-'DAY_OF_WEEK('/'DAY_OF_WEEK)?);
DAY_OF_WEEK:       [0-7];
