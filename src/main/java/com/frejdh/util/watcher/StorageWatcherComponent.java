package com.frejdh.util.watcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

/**
 *	Handles one specific directory for the directory watcher.
 */
class StorageWatcherComponent {

	public final StorageWatcherProperties properties;
	public final WatchService watcher;

	StorageWatcherComponent(@NotNull StorageWatcherProperties properties) throws IOException {
		this.properties = properties;
		this.watcher = FileSystems.getDefault().newWatchService();
		registerWatcher();
	}

	private void registerWatcher() throws IOException {
		this.properties.directory.register(this.watcher, this.properties.eventsToWatch.toArray());
	}
}
