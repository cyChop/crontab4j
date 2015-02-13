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

## Interesting links

### CRON reference

* [Unix man crontab][7]

### Other CRON-orientated tools

* [cron4j][8]
* [cron-utils][9]
* [Quartz][10] (but CRONs are only a little part of it)

## TODO

* Prepare generic parser, make compatible with following syntaxes:
  1. Unix
  2. cron4j
  3. Quartz
* Utilities
  1. Validator/Parser (first version will use Regexes)
  2. Cron expression (able to find next occurrence)
  3. Scheduler
  4. Optimizer
    * Optimisation strategies:
      * Repetition with step == ``1`` => range
      * Multiple expressions with at least one ``*`` (e.g. ``1-5,*,20-30/9``) => ``*``
      * Overlapping ranges (or repeats with same step) => Single range (or repeat with same step)
      * Identical ranges with multiple steps (``*/4,*/2``) => ``*/2``
  5. Descriptor (low priority)
  6. Generator (lower priority)
* V2.0 : test moving to Antlr and compare performances

[1]: http://img.shields.io/travis/cyChop/cron4j/master.svg
[2]: https://travis-ci.org/cyChop/cron4j
[3]: http://img.shields.io/coveralls/cyChop/cron4j/master.svg
[4]: https://coveralls.io/r/cyChop/cron4j?branch=master
[5]: https://img.shields.io/badge/license-MIT-blue.svg
[6]: http://opensource.org/licenses/MIT
[7]: http://www.unix.com/man-page/linux/5/crontab/
[8]: http://www.sauronsoftware.it/projects/cron4j/
[9]: https://github.com/jmrozanec/cron-utils
[10]: http://quartz-scheduler.org/