package com.frejdh.util.watcher;
import com.frejdh.util.ImmutableCollection;

import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Watch files inside a directory.
 */
public class StorageWatcher extends Thread {

	public interface OnChanged {
		void onChanged();
	}

	public final ImmutableCollection<StorageWatcherComponent> components;
	public final long interval;
	public final TimeUnit intervalUnit;

	StorageWatcher(List<StorageWatcherComponent> components, long interval, TimeUnit intervalUnit) {
		this.components = new ImmutableCollection<>(components);
		this.interval = interval;
		this.intervalUnit = intervalUnit;
	}

	StorageWatcher(List<StorageWatcherComponent> components) {
		this(components, 250, TimeUnit.MILLISECONDS);
	}

	public void run() {
		try {
			while (true) {
				for (StorageWatcherComponent component : components) {
					WatchKey wk;
					try {
						wk = component.watcher.poll(interval, intervalUnit);
					} catch (InterruptedException e) {
						return;
					}

					if (wk == null) {
						Thread.yield();
						continue;
					}

					for (WatchEvent<?> event : wk.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = ev.context();

						if (kind == StandardWatchEventKinds.OVERFLOW) {
							Thread.yield();
							continue;
						} else if (component.properties.eventsToWatch.contains((WatchEvent.Kind<Path>) kind) &&
								(component.properties.isWatchingAllFiles() || component.properties.files.contains(filename.toString()))) {
							component.properties.onChanged.onChanged();
						}
						boolean valid = wk.reset();
						if (!valid) { break; }
					}
					Thread.yield();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
