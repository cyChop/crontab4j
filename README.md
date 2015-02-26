# crontab4j

[![Build Status][1]][2]
[![Coverage Status][3]][4]
[![License][5]][6]

## Quick start

### Goal

This library allows for the triggering of a Java job based on a CRON expression.

### Supported CRON syntaxes

#### At the moment

* [Unix][7]
  * Classic integer ranges
  * Day and month names (3 letters, case insensitive)
  * Special expressions, with the exception of ``@reboot``

#### Planned for later

* [cron4j][8]
* [Quartz][10] (but CRONs are only a little part of it)
* crontab4j (take the best of each world and make it the most flexible as can be)

### How to use it

```java
import org.keyboardplaying.cron.parser.UnixCronParser;
import org.keyboardplaying.cron.scheduler.CronScheduler;

public class CronStarter {

    public static void main(String[] args) {
        CronScheduler schd = new CronScheduler();
        // Set parser: only Unix at the moment, but more to come
        schd.setParser(new UnixCronParser());
        schd.scheduleJob(new Runnable() {
            public void run() {
                System.out.println("Another minute ticked.");
            }
        }, "* * * * *");
        // schd is a daemon: it will not prevent the JVM from stopping
    }
}
```

### Daemon

The JVM will stop automatically if all remaining threads are daemons. The ``CronScheduler`` is a daemon
by default. To prevent this behaviour, you should instantiate it this way:
```java
CronScheduler schd = new CronScheduler(false);
```

A fair warning: you will have to stop it for the JVM to close:
```java
schd.terminate();
```

### Using with Spring

```xml
<bean id="schd" class="org.keyboardplaying.cron.scheduler.CronScheduler">
    <property name="parser"><bean class="org.keyboardplaying.cron.parser.UnixCronParser"/></property>
    <property name="jobs">
        <list>
            <ref bean="job1"/>
            <ref bean="job2"/>
            ...
        </list>
    </property>
</bean>

<bean id="job1" class="org.keyboardplaying.cron.scheduler.CronJob">
    <property name="job" ref="myRunnableBean"/>
    <property name="cron" value="0 0 * * *"/>
</bean>

...
```

## Interesting links

* [Unix man crontab][7]
* [Quartz][10] (but CRONs are only a little part of it)
* [cron4j][8]
* [cron-utils][9]

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

## Roadmap

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
  * [ ] Include license in jar (META-INF; avoid file duplication if possible)
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
[5]: https://img.shields.io/badge/license-BSD_3--Clause-blue.svg
[6]: http://opensource.org/licenses/BSD-3-Clause
[7]: http://www.unix.com/man-page/linux/5/crontab/
[8]: http://www.sauronsoftware.it/projects/cron4j/
[9]: https://github.com/jmrozanec/cron-utils
[10]: http://quartz-scheduler.org/
[11]: http://www.joda.org/joda-time/
