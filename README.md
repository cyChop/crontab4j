# crontab4j

[![Build Status][1]][2]
[![Coverage Status][3]][4]
[![License][5]][6]

## History

It happened at work: we wanted to gain the flexibility of CRON expressions for job scheduling, but
my boss feared Quartz was overkill for a single job.

So I gathered my knowledge about the CRONs (which was few, so I had to look for some more) and
created my own parser and scheduler. Not that hard, actually, but this was a kind of a draft.

I had the wish to go from scratch and write something a bit more elaborate and clean. And when
looking for the name, I discovered [``cron4j``][8] was already taken by a similar project. Still,
for the challenge...

My first try had been using [``joda-time``][11] for comfort and ease of use. However, this time, I
chose to avoid external libraries as much as possible in order to keep the footprint as light as
possible, and therefore used ``java.util.Calendar`` instead.

## Interesting links

### CRON reference

* [Unix man crontab][7]

### Other CRON-orientated Java tools

* [cron4j][8]
* [Quartz][10] (but CRONs are only a little part of it)
* [cron-utils][9]

## TODO

### V0.1 - The basic needs

  * [x] Cron expression
  * [x] Validator/Parser (parses CRON into POJO; v1.0: Regex and Unix-like CRONs only)
  * [x] Predictor (computes next occurrence)
  * [x] Scheduler
  * [ ] Finalizing
    * [ ] Missing tests
      * [x] Test ``allows`` for day with all types of DayConstraint
      * [ ] Test predictor with MultipleRules
    * [ ] Documentation
      * [x] Javadoc
      * [ ] README.md: quick use guide/code samples
    * [x] Cleaning and formatting
    * [ ] Generate MANIFEST.MF

### V0.2 - Changing the parsing engine and adding syntaxes

  * [ ] Test rewriting engine in Antlr (using a grammar has advantages in terms of readibility,
scalibility and maintenance)
  * [ ] Compare performances/footprint and retain overall best version
  * [ ] Scheduler enhancement
    * [ ] Use a custom implementation instead of Java's timer
    * [ ] Concurrence management (per job!)
  * [ ] Minor enhancements (search for TODO and FIXME in code)
  * [ ] Create a module ``crontab4j-scheduler``, containing one default syntax
    * [ ] Keep the best of each world
      * cron4j: possibility of multiple expressions
      * Quartz: seconds and years, advanced day management
  * [ ] Create a module ``crontab4j-syntaxes`` for additional syntaxes
    * [x] Unix
    * [ ] cron4j
    * [ ] Quartz

### V1.0 - Available for release

  * [ ] Ensure functionality
  * [ ] Documentation

### V1.1 - The utilities

  * [ ] Enhancements
    * [ ] Ensure min < max in ranges when validating/parsing
  * [ ] Optimizer (parses the rules and rewrite them for optimization)
    * [ ] Single values with a step (``1/2`` -> ``1``)
    * [ ] Remove CRON expressions without a next occurrence
    * [ ] Repetition with step == ``1`` -> range
    * [ ] Multiple expressions with at least one ``*`` (e.g. ``1-5,*,20-30/9``) -> ``*``
    * [ ] Overlapping ranges (or repeats with same step) -> Single range (or repeat with same step)
    * [ ] Identical ranges with multiple steps (``*/4,*/2``) -> ``*/2``~
    * [ ] Out of access ranges (``0 0 2-7/2 31 *`` -> 31st of every even month until July)
  * [ ] Generator (related to previous; reverts a ``CronExpression`` to a ``String``)
  * [ ] Descriptor (describes a CRON in natural language; low priority)
  * [ ] Split into ``crontab4j-core`` and ``crontab4j-utils`` to reduce footprint if need be
  * [ ] Review documentation

[1]: http://img.shields.io/travis/cyChop/crontab4j/master.svg
[2]: https://travis-ci.org/cyChop/crontab4j
[3]: http://img.shields.io/coveralls/cyChop/crontab4j/master.svg
[4]: https://coveralls.io/r/cyChop/crontab4j?branch=master
[5]: https://img.shields.io/badge/license-BSD-blue.svg
[6]: http://opensource.org/licenses/BSD-3-Clause
[7]: http://www.unix.com/man-page/linux/5/crontab/
[8]: http://www.sauronsoftware.it/projects/cron4j/
[9]: https://github.com/jmrozanec/cron-utils
[10]: http://quartz-scheduler.org/
[11]: http://www.joda.org/joda-time/
