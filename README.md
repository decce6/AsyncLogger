**Async Logger** is a Minecraft mod that makes logging operations asynchronous.

Vanilla uses synchronous loggers. This means the actual logging operations, including I/O operations, happen on the logging thread. This mod changes logging to be asynchronous for better performance.

This can be especially useful when large amounts of messages are being logged and/or debug logging is enabled (default on Forge / NeoForge).

## Technical Details

- [Asynchronous loggers](https://logging.apache.org/log4j/2.x/manual/async.html) are used, in favor of Asynchronous appenders. The former is newer, faster and lighter on allocation rate.
  - Asynchronous loggers require LMAX Disruptor, a lock-free inter-thread communication library. The mod will load the library onto the system classloader when it is not present.
- The config file allows easy control of some log4j2 settings.
- `System.out` and `System.err` are wrapped to ensure coherent logging order.
