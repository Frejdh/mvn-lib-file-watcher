File & directory watcher
-
Easily watch files and/or directories for creation/modification/deletion events.
Every watcher will be executed on a new thread, where each watcher can be combined with multiple actions for different files/directories.

## Guide
How to use and add different watchers. 

### The basics
The builder can be retrieved using either one of these lines of code (they're equivalent):

### Creating the StorageWatcher instance
```java
StorageWatcherBuilder builder1 = new StorageWatcherBuilder();
StorageWatcherBuilder builder2 = StorageWatcherBuilder.getBuilder();
```

And the watcher itself is generated by calling the `build()` method inside of the builder instance:
```java
StorageWatcherBuilder builder = new StorageWatcherBuilder();
StorageWatcher watcher = 
        builder.
        // Configuration...
        .build();
```

Start the new watcher thread by executing the `start()` method.
```java
watcher.start();
```

### Configuration examples
Complete examples.

#### Simple usage (watch one file)
A scenario where you want to watch one file.
```java
StorageWatcher watcher = new StorageWatcherBuilder()
        .specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
        .watchFile(filename)
        .onChanged(() -> {
            logger.info("Do whatever");
        })
        .build();
watcher.start();
```

#### Nested usage (watch one file, but do different actions)
Another scenario where different use-cases are required,
but where they should still share the same thread.
```java
StorageWatcher watcher = StorageWatcherBuilder.getBuilder()
        .specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
        .watchFile(filename)
        .onChanged(() -> {
            logger.info("Creation... Do whatever");
        })
        .createNext()   // The important bit (next configuration)
        .specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
        .watchFile(filename)
        .onChanged(() -> {
            logger.info("Modification... Do whatever");
        })
        .build();
watcher.start();
```

#### Watch different files/directories with custom interval
Another scenario where different use-cases are required,
but where they should still share the same thread.
```java
StorageWatcher watcher = StorageWatcherBuilder.getBuilder()
        .interval(5, TimeUnit.SECONDS)   // Shared value between all watcher configurations
        .specifyEvents(StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
        .watchDirectories(directory1, directory2)
        .onChanged(() -> {
            logger.info("Deletion... Do whatever");
        })
        .createNext()   // The important bit (next configuration)
        .specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
        .watchFiles(filename1, filename2, filename3)
        .onChanged(() -> {
            logger.info("Modification... Do whatever");
        })
        .build();
watcher.start();
```

## Adding the dependency

```
<dependencies>
    <dependency>
        <groupId>com.frejdh.util</groupId>
        <artifactId>file-watcher</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>

<repositories> <!-- Required in order to resolve this package -->
    <repository>
        <id>mvn-lib-file-watcher</id>
        <url>https://raw.github.com/Frejdh/mvn-lib-file-watcher/releases/</url>
    </repository>
</repositories>
```

## Other libraries
[Search for my other public libraries here](https://github.com/search?q=Frejdh%2Fmvn-lib-).