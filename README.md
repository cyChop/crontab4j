# Crontab4j

[travis-badge]: https://img.shields.io/travis/cyChop/crontab4j.svg
[travis]: https://travis-ci.org/cyChop/crontab4j
[sonarc-badge]: https://img.shields.io/sonar/https/sonarqube.com/org.keyboardplaying:crontab4j/coverage.svg
[sonarc]: https://sonarqube.com/overview/coverage?id=org.keyboardplaying:crontab4j
[sonarq-badge]: https://img.shields.io/sonar/https/sonarqube.com/org.keyboardplaying:crontab4j/tech_debt.svg
[sonarq]: https://sonarqube.com/overview/debt?id=org.keyboardplaying:crontab4j
[issues-badge]: https://img.shields.io/github/issues-raw/cyChop/crontab4j.svg
[issues]: https://github.com/cyChop/crontab4j/issues
[waffle]: https://waffle.io/cyChop/crontab4j
[licens-badge]: https://img.shields.io/github/license/cyChop/crontab4j.svg
[licens]: https://opensource.org/licenses/MIT

[![Build status][travis-badge]][travis]
[![Test coverage][sonarc-badge]][sonarc]
[![Technical debt][sonarq-badge]][sonarq]
[![Issues][issues-badge]][waffle]
[![License: MIT][licens-badge]][licens]

[url-cron-unix]: http://www.unix.com/man-page/linux/5/crontab/
[url-cron-cron4j]: http://www.sauronsoftware.it/projects/cron4j/
[url-cron-utils]: https://github.com/jmrozanec/cron-utils
[url-cron-quartz]: http://quartz-scheduler.org/
[url-joda-time]: http://www.joda.org/joda-time/

## Quick start

### Goal

This library allows for the triggering of a Java job based on a CRON expression.

### Supported CRON syntaxes

#### At the moment

[Unix][url-cron-unix]
* Classic integer ranges
* Day and month names (3 letters, case insensitive)
* Special expressions, with the exception of `@reboot`

#### Planned for later

* [cron4j][url-cron-cron4j]
* [Quartz][url-cron-quartz] (but CRONs are only a little part of it)
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

The JVM will stop automatically if all remaining threads are daemons. The `CronScheduler` is a daemon
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
```

## Interesting links

* [Unix man crontab][url-cron-unix]
* [Quartz][url-cron-quartz] (but CRONs are only a little part of it)
* [cron4j][url-cron-cron4j]
* [cron-utils][url-cron-utils]

## History

It happened at work: we wanted to gain the flexibility of CRON expressions for job scheduling, but
my boss feared Quartz was overkill for a single job.

So I gathered my knowledge about the CRONs (which was few, so I had to look for some more) and
created my own parser and scheduler. Not that hard, actually, but this was a kind of a draft.

I had the wish to go from scratch and write something a bit more elaborate and clean. And when
looking for the name, I discovered [`cron4j`][url-cron-cron4j] was already taken by a similar project. Still,
for the challenge...

My first try had been using [`joda-time`][url-joda-time] for comfort and ease of use. However, this time, I
chose to avoid external libraries as much as possible in order to keep the footprint as light as
possible, and therefore used `java.util.Calendar` instead.

## Roadmap

### V0.2 - Changing the parsing engine and adding syntaxes

* [ ] Find a parsing method without use of regular expressions
* [ ] Scheduler enhancement
 * [ ] Use a custom implementation instead of Java's timer
 * [ ] Concurrence management (per job!)
* [ ] Minor enhancements (search for TODO and FIXME in code)
* [ ] Create a module `crontab4j-scheduler`, containing one default syntax
 * [ ] Keep the best of each world
  * cron4j: possibility of multiple expressions
  * Quartz: seconds and years, advanced day management
* [ ] Create a module `crontab4j-syntaxes` for additional syntaxes
 * [x] Unix
 * [ ] cron4j
 * [ ] Quartz
* [ ] Drop `Calendar` and use Java 8's time utilities

### V1.0 - Available for release

* [ ] Ensure functionality
* [ ] Include license in jar (META-INF; avoid file duplication if possible)
* [ ] Documentation

### V1.1 - The utilities

* [ ] Enhancements
 * [ ] Ensure min < max in ranges when validating/parsing
* [ ] Optimizer (parses the rules and rewrite them for optimization)
 * [ ] Single values with a step (`1/2` -> `1`)
 * [ ] Remove CRON expressions without a next occurrence
 * [ ] Repetition with step ## `1` -> range
 * [ ] Multiple expressions with at least one `*` (e.g. `1-5,*,20-30/9`) -> `*`
 * [ ] Overlapping ranges (or repeats with same step) -> Single range (or repeat with same step)
 * [ ] Identical ranges with multiple steps (`*/4,*/2`) -> `*/2`
 * [ ] Out of access ranges (`0 0 2-7/2 31 *` -> 31st of every even month until July)
* [ ] Generator (related to previous; reverts a `CronExpression` to a `String`)
* [ ] Descriptor (describes a CRON in natural language; low priority)
* [ ] Split into `crontab4j-core` and `crontab4j-utils` to reduce footprint if need be
* [ ] Review documentation
