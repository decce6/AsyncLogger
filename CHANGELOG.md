## 2.1.2

- The `noDebugLog` config now cleans up archived debug logs ([#13](https://github.com/decce6/AsyncLogger/pull/13))
- Fixed the issue where log format may appear different than without mod, like log name missing

## 2.1.1

Fixed `java.nio.file.FileSystemAlreadyExistsException` crash on startup. ([#12](https://github.com/decce6/AsyncLogger/pull/12))

## 2.1.0

- Fixed mod not working on 1.21.1 NeoForge
- Improved compatibility with mods that register appenders early
- Improved dedicated server support
  - Note on side requirement: this mod does not have strict side requirement. You can install it on the client side without it being present on the server, and vice versa. It will function on the side it is installed on.
- Added ability to collect extra filters from `config/asynclogger/*.toml`
  - By writing filters there, you can format your filters as you like and add comments
  - Adding filters in the main config file remain supported
- Improved `noDebugLog` option by deleting existing `debug.log` on logger configuration

## 2.0.1

- Fixed `java.nio.file.ProviderNotFoundException: Provider "jar" not found` crashes with specific mods.

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