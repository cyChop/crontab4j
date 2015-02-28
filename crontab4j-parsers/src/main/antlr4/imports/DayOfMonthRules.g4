grammar DayOfMonthRules;
import Rule;
dayOfMonthRules: atomicRule(','atomicRule)*;
UNIT: '3'[0-1]|[0-2]?[0-9];
