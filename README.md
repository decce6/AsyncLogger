**Async Logger** is a Minecraft mod that makes logging operations asynchronous.

In vanilla, loggers are not asynchronous. This means the actual logging operations, including I/O operations, happen on the logging thread. This mod changes logging to be asynchronous for better performance.

This can be especially useful when large amounts of messages are being logged and/or debug logging is enabled (default on Forge / NeoForge).

## Benchmarks

| Item                             | Vanilla   | AsyncLogger |
|----------------------------------|-----------|-------------|
| Simple Messages (10k)            | 6146.1ms  | 141.2ms     |
| Messages with Placeholders (10k) | 6413.2ms  | 96.6ms      |
| Messages with Throwable (1.5k)   | 19572.4ms | 9.0ms       |

You can do these tests yourself by enabling `testPerformance` in the config. When this option is enabled, the mod will log a large number of messages both before and after setting up async loggers. You can see the test results in the log.

## Technical Details

- [Asynchronous loggers](https://logging.apache.org/log4j/2.x/manual/async.html) are used, in favor of Asynchronous appenders. The former is newer, faster and lighter on allocation rate.
  - Asynchronous loggers require LMAX Disruptor, a lock-free inter-thread communication library. The mod will load the library onto the system classloader when it is not present. This mod redistributes LMAX Disruptor under the Apache-2.0 [License](https://github.com/LMAX-Exchange/disruptor/blob/master/LICENCE.txt).
- The config file allows easy control of some log4j2 settings.
- `System.out` and `System.err` can be redirected to loggers.
