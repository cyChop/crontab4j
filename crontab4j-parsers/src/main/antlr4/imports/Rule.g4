grammar Rule;
ruleSet:			atomicRule(','atomicRule)*;
atomicRule:         anyValueRule|singleValueRule|rangeRule|repeatRule;
anyValueRule:       ANY;
singleValueRule:    value;
rangeRule:          min'-'max;
repeatRule:         (ANY|min'-'max)'/'step;

value:				nameableUnit;
min:				nameableUnit;
max:				nameableUnit;
step:				UNIT;

ANY:                '*';

/** This can be overridden in children grammar. */
nameableUnit:       UNIT;
/** Unit must be explicitly declared in children grammars. */
