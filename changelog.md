# Changelog
Changes for each version.

## 1.0.2
- Added `stop()` method for the file watcher's thread execution.
- Updated parent version in `pom.xml`.

## 1.0.1
- Removed `Thread` extension for `StorageWatcher` and instead opted for an internal thread variable. Reason: To avoid confusion between internal methods and `Thread` methods.
- Test added for relative resource.
- More examples in documentation.

## 1.0.0
First version
