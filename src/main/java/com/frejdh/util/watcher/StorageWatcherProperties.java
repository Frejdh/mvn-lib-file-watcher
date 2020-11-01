package com.frejdh.util.watcher;

import com.frejdh.util.ImmutableCollection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


class StorageWatcherProperties {

	public static final ImmutableCollection<WatchEvent.Kind<Path>> DEFAULT_WATCH_EVENT =
			new ImmutableCollection<>(
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE
			);
	public static final long DEFAULT_WATCHER_INTERVAL = 250L;
	public static final TimeUnit DEFAULT_WATCHER_INTERVAL_UNIT = TimeUnit.MILLISECONDS;

	public final ImmutableCollection<String> files;
	public final ImmutableCollection<WatchEvent.Kind<Path>> eventsToWatch;
	public final Path directory;
	public final StorageWatcher.OnChanged onChanged;

	public final long watcherInterval;
	public final TimeUnit watcherIntervalUnit;

	StorageWatcherProperties(ImmutableCollection<String> files,
							 @Nullable ImmutableCollection<WatchEvent.Kind<Path>> eventsToWatch,
							 String directory,
							 StorageWatcher.OnChanged onChanged,
							 @Nullable Long watcherInterval,
							 @Nullable TimeUnit watcherIntervalUnit) {
		this.files = files;
		this.eventsToWatch = eventsToWatch == null || eventsToWatch.isEmpty() ? DEFAULT_WATCH_EVENT : eventsToWatch;
		this.directory = FileSystems.getDefault().getPath(directory);
		this.onChanged = onChanged;
		this.watcherInterval = watcherInterval != null ? watcherInterval : DEFAULT_WATCHER_INTERVAL;
		this.watcherIntervalUnit = watcherIntervalUnit != null ? watcherIntervalUnit : DEFAULT_WATCHER_INTERVAL_UNIT;
	}

	StorageWatcherProperties(Collection<String> files,
							 @Nullable Collection<WatchEvent.Kind<Path>> eventsToWatch,
							 String directory,
							 StorageWatcher.OnChanged onChanged,
							 @Nullable Long watcherInterval,
							 @Nullable TimeUnit watcherIntervalUnit) {
		this(new ImmutableCollection<>(files),
			 new ImmutableCollection<>(eventsToWatch),
			 directory,
			 onChanged,
			 watcherInterval,
			 watcherIntervalUnit
		);
	}

	public boolean isWatchingAllFiles() {
		return files.size() == 0;
	}

	static class EventsForFile {
		public final String filename;
		public final ImmutableCollection<WatchEvent.Kind<Path>> events;

		public EventsForFile(String filename, ImmutableCollection<WatchEvent.Kind<Path>> events) {
			this.filename = filename;
			this.events = events;
		}

		public EventsForFile(String filename, Collection<WatchEvent.Kind<Path>> events) {
			this(filename, new ImmutableCollection<>(events));
		}
	}

}
