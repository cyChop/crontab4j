grammar HourRules;
import Rule;
hourRules: atomicRule(','atomicRule)*;
UNIT: '2'[0-3]|'1'?[0-9];
