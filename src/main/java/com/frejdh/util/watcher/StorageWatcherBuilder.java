package com.frejdh.util.watcher;

import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Builder for the @{@link StorageWatcher} class.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class StorageWatcherBuilder {

	private StorageWatcherBuilder parentBuilder = null;
	private final Set<WatchEvent.Kind<Path>> eventsToWatch = new HashSet<>();
	private final Set<String> filesToLimitTo = new HashSet<>();
	private final Set<String> directoriesToWatch = new HashSet<>();
	private StorageWatcher.OnChanged onChanged = (directory, filename) -> { };
	private Long watcherInterval;
	private TimeUnit watcherIntervalUnit;

	/**
	 * Create a builder.
	 */
	public StorageWatcherBuilder() { }

	private StorageWatcherBuilder(StorageWatcherBuilder parent) {
		this.parentBuilder = parent;
	}

	/**
	 * Equivalent to calling the empty constructor {@link #StorageWatcherBuilder()}
	 * @return A new builder
	 */
	public static StorageWatcherBuilder getBuilder() {
		return new StorageWatcherBuilder();
	}

	/**
	 * Events to watch for such as: modify, create or delete. Can be combined with multiple calls and/or {@link #specifyEvents(WatchEvent.Kind[])}.
	 * Use the static variables defined in {@link java.nio.file.StandardWatchEventKinds}. For example: <br>
	 * <code>
	 * StandardWatchEventKinds.ENTRY_CREATE
	 * </code>
	 * @param event Event to watch.
	 * @return The same builder instance.
	 */
	public StorageWatcherBuilder specifyEvent(WatchEvent.Kind<Path> event) {
		eventsToWatch.add(event);
		return this;
	}

	/**
	 * The same as calling {@link #specifyEvent(WatchEvent.Kind)} multiple times. Can be combined with {@link #specifyEvent(WatchEvent.Kind)}.
	 * @param events Events to watch
	 * @return The same builder instance.
	 */
	@SafeVarargs
	public final StorageWatcherBuilder specifyEvents(WatchEvent.Kind<Path>... events) {
		eventsToWatch.addAll(Arrays.asList(events));
		return this;
	}

	/**
	 * See {@link #specifyEvents(WatchEvent.Kind[])}.
	 */
	public StorageWatcherBuilder specifyEvents(Collection<WatchEvent.Kind<Path>> events) {
		eventsToWatch.addAll(events);
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
	 * The same as calling {@link #watchFile(String)} multiple times. Can be combined with {@link #watchFile(String)}
	 * @param filenames Files to watch
	 * @return The builder reference
	 */
	public StorageWatcherBuilder watchFiles(String... filenames) {
		filesToLimitTo.addAll(Arrays.asList(filenames));
		return this;
	}

	/**
	 * See {@link #watchFiles(String...)}.
	 */
	public StorageWatcherBuilder watchFiles(Collection<String> filenames) {
		filesToLimitTo.addAll(filenames);
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
	 * The same as calling {@link #watchDirectory(String)} multiple times. Can be combined with {@link #watchDirectory(String)}
	 * @param directories Directories to watch
	 * @return The same builder reference
	 */
	public StorageWatcherBuilder watchDirectories(String... directories) {
		directoriesToWatch.addAll(Arrays.asList(directories));
		return this;
	}

	/**
	 * See {@link #watchDirectories(String...)}.
	 */
	public StorageWatcherBuilder watchDirectories(Collection<String> directories) {
		directoriesToWatch.addAll(directories);
		return this;
	}

	/**
	 * Set the interval for checking the directories/files.
	 * <strong>Shared between all of the watcher components!</strong>
	 * @param watcherInterval Interval to check. (Null or <= 0) = {@link StorageWatcherProperties#DEFAULT_WATCHER_INTERVAL}
	 * @param watcherIntervalUnit Unit for the interval. Null = {@link StorageWatcherProperties#DEFAULT_WATCHER_INTERVAL_UNIT}
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

	/**
	 * Same as {@link #interval(Long, TimeUnit)}, but which accepts an integer as parameter.
	 */
	public StorageWatcherBuilder interval(@Nullable Integer watcherInterval, @Nullable TimeUnit watcherIntervalUnit) {
		return interval((watcherInterval != null) ? Integer.toUnsignedLong(watcherInterval) : null, watcherIntervalUnit);
	}

	/**
	 * Set what to do whenever an event is detected. For example:
	 * <code>
	 * new StorageWatcherBuilder()
	 *     .onChanged((directory, filename) -> {
	 *         System.out.println("Affected directory: " + directory + ", and file: " + filename);
	 *     })
	 *     .start();
	 * </code>
	 * @param onChanged
	 * @return The same builder instance.
	 */
	public StorageWatcherBuilder onChanged(StorageWatcher.OnChanged onChanged) {
		this.onChanged = onChanged;
		return this;
	}

	/**
	 * Creates a new Watcher that can be configured.
	 * Not to be misstaken for {@link #build()}
	 * @return A <strong>new</strong> builder instance
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
				buildComponents(new ArrayList<>()),
				watcherInterval,
				watcherIntervalUnit
		);
	}

	/**
	 * Helper method. Builds all of the components that should be used by the watcher.
	 * @param currentComponents List of the current components (for recursive usages).
	 * @return The final list with all of the components.
	 */
	private List<StorageWatcherComponent> buildComponents(List<StorageWatcherComponent> currentComponents) {
		for (Map.Entry<String, Set<String>> grouping : groupByDirectories(directoriesToWatch, filesToLimitTo).entrySet()) {
			StorageWatcherProperties settings = new StorageWatcherProperties(
					grouping.getValue(),
					eventsToWatch,
					grouping.getKey(),
					onChanged
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
			return parentBuilder.buildComponents(currentComponents);
		}
		return currentComponents;
	}

	/**
	 * Helper method. Group everything by directories.
	 * @param directories The directories to watch.
	 * @param filenames The files to watch.
	 * @return A map with the directory as key, and a collection of files as the value.
	 */
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
