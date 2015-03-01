grammar Cron;

cron:	            	'@'specialExpr | regular;

specialExpr:			'reboot' | 'yearly' | 'annually' | 'monthly' | 'weekly' | 'daily' | 'midnight' | 'hourly';

regular:				minuteField WS hourField WS dayOfMonthField WS monthField WS dayOfWeekField;
minuteField:			field;
hourField:				field;
dayOfMonthField:		field;
monthField:				field;
dayOfWeekField:			field;
field: 					ATOM(','ATOM)*;

ATOM:					([^0-9]+|'*');
WS:						(' ')+;
