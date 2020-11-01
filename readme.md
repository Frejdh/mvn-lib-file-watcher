File & directory watcher
-
Easily watch files and/or directories for creation/modification/deletion events.
All done on a new thread!

## Guide
How to use and add.

### Simple usage
A scenario where you want to watch one file/directory.
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

### Nested usage
Another scenario where different use-cases are required,
but where they should still share the same thread.
```java
StorageWatcher watcher = StorageWatcherBuilder.getBuilder()
        .specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
        .watchFile(filename)
        .onChanged(() -> {
            logger.info("Creation... Do whatever");
        })
        .createNext()   // The important bit!
        .specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
        .watchFile(filename)
        .onChanged(() -> {
            logger.info("Modification... Do whatever");
        })
        .build();
watcher.start();
```

### Adding the dependency

```
<dependencies>
    <dependency>
        <groupId>com.frejdh.util</groupId>
        <artifactId>file-watcher</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<repositories> <!-- Required in order to resolve this package -->
    <repository>
        <id>mvn-lib-file-watcher</id>
        <url>https://raw.github.com/Frejdh/mvn-lib-file-watcher/mvn-repo/</url>
    </repository>
</repositories>
```

## Other libraries
[Search for my other public libraries here](https://github.com/search?q=Frejdh%2Fmvn-lib-).
