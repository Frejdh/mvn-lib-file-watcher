package com.frejdh.util.watcher;

import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class StorageWatcherBuilder {

	private StorageWatcherBuilder parentBuilder = null;
	private final Set<WatchEvent.Kind<Path>> eventsToWatch = new HashSet<>();
	private final Set<String> filesToLimitTo = new HashSet<>();
	private final Set<String> directoriesToWatch = new HashSet<>();
	private StorageWatcher.OnChanged onChanged = () -> { };
	private Long watcherInterval;
	private TimeUnit watcherIntervalUnit;

	public StorageWatcherBuilder() { }

	private StorageWatcherBuilder(StorageWatcherBuilder parent) {
		this.parentBuilder = parent;
	}

	/**
	 * Same as:
	 * <code>
	 *     new StorageWatcherBuilder();
	 * </code>
	 * @return A new builder
	 */
	public static StorageWatcherBuilder getBuilder() {
		return new StorageWatcherBuilder();
	}

	/**
	 * Use java.nio.file.StandardWatchEventKinds
	 */
	public StorageWatcherBuilder specifyEvent(WatchEvent.Kind<Path> event) {
		eventsToWatch.add(event);
		return this;
	}

	/**
	 * Use java.nio.file.StandardWatchEventKinds
	 */
	@SafeVarargs
	public final StorageWatcherBuilder specifyEvents(WatchEvent.Kind<Path>... events) {
		eventsToWatch.addAll(Arrays.asList(events));
		return this;
	}

	/**
	 * Can be combined with multiple calls and/or {@link #watchFiles(String...)}
	 * @param filename File to watch
	 * @return The builder reference
	 */
	public StorageWatcherBuilder watchFile(String filename) {
		filesToLimitTo.add(filename);
		return this;
	}

	/**
	 * Can be combined with multiple calls and/or {@link #watchFile(String)}
	 * @param filenames Files to watch
	 * @return The builder reference
	 */
	public StorageWatcherBuilder watchFiles(String... filenames) {
		filesToLimitTo.addAll(Arrays.asList(filenames));
		return this;
	}

	/**
	 * Can be combined with multiple calls and/or {@link #watchDirectories(String...)}
	 * @param directory Directory to watch
	 * @return The same builder reference
	 */
	public StorageWatcherBuilder watchDirectory(String directory) {
		directoriesToWatch.add(directory);
		return this;
	}

	/**
	 * Can be combined with multiple calls and/or {@link #watchDirectory(String)}
	 * @param directories Directories to watch
	 * @return The same builder reference
	 */
	public StorageWatcherBuilder watchDirectories(String... directories) {
		directoriesToWatch.addAll(Arrays.asList(directories));
		return this;
	}

	/**
	 * Set the interval for checking the directories/files.
	 * @param watcherInterval Interval to check. Null or <= 0: {@link StorageWatcherProperties#DEFAULT_WATCHER_INTERVAL}
	 * @param watcherIntervalUnit Unit for the interval. Null: {@link StorageWatcherProperties#DEFAULT_WATCHER_INTERVAL_UNIT}
	 * @return The same builder reference
	 */
	public StorageWatcherBuilder interval(@Nullable Long watcherInterval, @Nullable TimeUnit watcherIntervalUnit) {
		this.watcherInterval = (watcherInterval == null || watcherInterval <= 0)
				? this.watcherInterval = StorageWatcherProperties.DEFAULT_WATCHER_INTERVAL
				: watcherInterval;

		this.watcherIntervalUnit = (watcherIntervalUnit == null)
				? StorageWatcherProperties.DEFAULT_WATCHER_INTERVAL_UNIT
				: watcherIntervalUnit;

		return this;
	}

	public StorageWatcherBuilder onChanged(StorageWatcher.OnChanged onChanged) {
		this.onChanged = onChanged;
		return this;
	}

	/**
	 * Creates a new Watcher that can be configured.
	 * Not to be misstaken for {@link #build()}
	 * @return A new builder instance
	 */
	public StorageWatcherBuilder createNext() {
		return new StorageWatcherBuilder(this);
	}

	/**
	 * Builds the {@link StorageWatcher} instance.
	 * @return A watcher instance.
	 */
	public StorageWatcher build() {
		return new StorageWatcher(
				build(new ArrayList<>())
		);

	}

	private List<StorageWatcherComponent> build(List<StorageWatcherComponent> currentComponents) {
		for (Map.Entry<String, Set<String>> grouping : groupByDirectories(directoriesToWatch, filesToLimitTo).entrySet()) {
			StorageWatcherProperties settings = new StorageWatcherProperties(
					grouping.getValue(),
					eventsToWatch,
					grouping.getKey(),
					onChanged,
					watcherInterval,
					watcherIntervalUnit
			);

			StorageWatcherComponent watcher = null;
			try {
				watcher = new StorageWatcherComponent(settings);
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentComponents.add(watcher);
		}

		if (parentBuilder != null) {
			return parentBuilder.build(currentComponents);
		}
		return currentComponents;
	}

	private Map<String, Set<String>> groupByDirectories(Set<String> directories, Set<String> filenames) {
		Map<String, Set<String>> group = new HashMap<>();

		directories.forEach(dir -> group.put(FileSystems.getDefault().getPath(dir).toAbsolutePath().toString(), new HashSet<>()));

		filenames.forEach(file -> {
			Path absolutePath = FileSystems.getDefault().getPath(file).toAbsolutePath();
			String parentDir = absolutePath.getParent() != null ? absolutePath.getParent().toString() : "";
			Set<String> set = group.getOrDefault(parentDir, new HashSet<>());
			set.add(absolutePath.getFileName().toString());
			group.put(parentDir, set);
		});

		return group;
	}

}
