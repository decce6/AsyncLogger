## 2.0.0

- Implemented asynchronous, efficient filtering
  - Filter by level, logger name, and message (match substring or regexps)
  - Filtering happens on the dedicated, async logging thread - no overhead on the caller thread
  - Can also optionally apply filters to `System#out` and `System#err`
- Added option to disable debug logging on Forge/NeoForge

## 1.1.4

Fixed a crash at startup on 1.19.2 Forge.

## 1.1.3

- Added settings for `asyncQueueFullPolicy` and `discardThreshold`
- Backported to 1.19.2 and 1.18.2

## 1.1.2

- Fixed excessive memory usage
- Fixed a typo in config comment

## 1.1.1

- Fixed JiJ discovery on Forge (lazyyyyy compatibility)

## 1.1.0

- Added config options for `formatMsgAsync`, `wrapSysOutSysErr`, and `testPerformance`
- Include disruptor license file in built jar
- Added 26.1 support

## 1.0.0

- Initial release