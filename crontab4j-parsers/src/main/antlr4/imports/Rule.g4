grammar Rule;
atomicRule:         anyValueRule|singleValueRule|rangeRule|repeatRule;
anyValueRule:       ANY;
singleValueRule:    nameableUnit;
rangeRule:          nameableUnit'-'nameableUnit;
repeatRule:         (ANY|rangeRule)'/'UNIT;
ANY:                '*';

/** This can be overridden in children grammar. */
nameableUnit:       UNIT;
/** Unit must be explicitly declared in children grammars. */
