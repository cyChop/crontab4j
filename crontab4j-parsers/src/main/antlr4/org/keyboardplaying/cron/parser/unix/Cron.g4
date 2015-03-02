grammar Cron;

cron:	            	'@'specialExpr | regular;

specialExpr:			'reboot' | 'yearly' | 'annually' | 'monthly' | 'weekly' | 'daily' | 'midnight' | 'hourly';

regular:				minuteField WS hourField WS dayOfMonthField WS monthField WS dayOfWeekField;
minuteField:			rules;
hourField:				rules;
dayOfMonthField:		rules;
monthField:				rules;
dayOfWeekField:			rules;

rules:					atomicRule(','atomicRule)*;
atomicRule:         	anyValueRule|singleValueRule|rangeRule|repeatRule;
anyValueRule:       	ANY;
singleValueRule:    	value;
rangeRule:          	min'-'max;
repeatRule:         	(ANY|min'-'max)'/'step;

value:					alias;
min:					alias;
max:					alias;
step:					UNIT;

alias:					UNIT|ALIAS;

ANY:                	'*';
ALIAS:					MONTH_ALIAS | DAY_ALIAS;
UNIT:					[0-9]+;
WS:						(' ')+;

fragment MONTH_ALIAS:	J A N | F E B | M A R | A P R | M A Y | J U N | J U L | A U G | S E P | O C T | N O V | D E C;
fragment DAY_ALIAS:		M O N | T U E | W E D | T H U | F R I | S A T | S U N;

fragment A:				[Aa];
fragment B:				[Bb];
fragment C:				[Cc];
fragment D:				[Dd];
fragment E:				[Ee];
fragment F:				[Ff];
fragment G:				[Gg];
fragment H:				[Hh];
fragment I:				[Ii];
fragment J:				[Jj];
/** fragment K:				[Kk]; */
fragment L:				[Ll];
fragment M:				[Mm];
fragment N:				[Nn];
fragment O:				[Oo];
fragment P:				[Pp];
/** fragment Q:				[Qq]; */
fragment R:				[Rr];
fragment S:				[Ss];
fragment T:				[Tt];
fragment U:				[Uu];
fragment V:				[Vv];
fragment W:				[Ww];
/** fragment X:				[Xx]; */
fragment Y:				[Yy];
/** fragment Z:				[Zz]; */
