# HTTP Snowflake

This is a fork of Twitter's Snowflake that has been considerably simplified for people not sharing all of Twitter's high end requirements.  For example:

- Scribe-based audit trails have been removed.
- Thrift RPC has been replaced by HTTP.
- Zookeeper coordination has been removed (for now).
- Maven has been replaced by sbt for compatibility with a standard "sbt clean compile stage" buildpack.
- Datacenter IDs have been removed; Worker IDs are enough.

Basically, this is an HTTP server with one route - `GET /id` - that returns a unique directly sortable ID.  You boot the server with one parameter, `workerId`, and you're done.

You can also easily use the ID generator in process:

    import com.transcriptic.snowflake.Worker

    val worker = new Worker(workerId)
    worker.nextId

And cut out the network latency.

## Requirements

### Performance
 * minimum 10k ids per second per process
 * response rate 2ms (plus network latency)

### Uncoordinated

For high availability within and across data centers, machines generating ids should not have to coordinate with each other.

### (Roughly) Time Ordered

We have a number of API resources that assume an ordering (they let you look things up "since this id").

However, as a result of a large number of asynchronous operations, we already don't guarantee in-order delivery.

We can guarantee, however, that the id numbers will be k-sorted (references: http://portal.acm.org/citation.cfm?id=70413.70419 and http://portal.acm.org/citation.cfm?id=110778.110783) within a reasonable bound (we're promising 1s, but shooting for 10's of ms).

### Directly Sortable

The ids should be sortable without loading the full objects that the represent. This sorting should be the above ordering.

### Compact

There are many otherwise reasonable solutions to this problem that require 128bit numbers. For various reasons, we need to keep our ids under 64bits.

### Highly Available

The id generation scheme should be at least as available as our related services (like our storage services).

##  Solution
* HTTP server written in Scala
* id is composed of:
  * time - 41 bits (millisecond precision w/ a custom epoch gives us 69 years)
  * configured machine id - 10 bits - gives us up to 1024 machines
  * sequence number - 12 bits - rolls over every 4096 per machine (with protection to avoid rollover in the same ms)

### System Clock Dependency

You should use NTP to keep your system clock accurate.  Snowflake protects from non-monotonic clocks, i.e. clocks that run backwards.  If your clock is running fast and NTP tells it to repeat a few milliseconds, snowflake will refuse to generate ids until a time that is after the last time we generated an id. Even better, run in a mode where ntp won't move the clock backwards. See http://wiki.dovecot.org/TimeMovedBackwards#Time_synchronization for tips on how to do this.