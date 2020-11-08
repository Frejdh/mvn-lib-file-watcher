package com.frejdh.util.watcher;
import com.frejdh.util.ImmutableCollection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Watch files inside a directory.
 */
public class StorageWatcher extends Thread {

	public interface OnChanged {
		void onChanged(String directory, String filename);
	}

	public final ImmutableCollection<StorageWatcherComponent> components;
	public final long interval;
	public final TimeUnit intervalUnit;

	public static final long DEFAULT_INTERVAL = 10;
	public static final TimeUnit DEFAULT_INTERVAL_UNIT = TimeUnit.SECONDS;

	/**
	 * Watcher thread for the configured storage properties.
	 * @param components Components to watch
	 * @param interval Interval to check for new changes.
	 * @param intervalUnit Interval unit.
	 */
	StorageWatcher(List<StorageWatcherComponent> components, Long interval, TimeUnit intervalUnit) {
		this.components = new ImmutableCollection<>(components);
		this.interval = (interval != null) ? interval : DEFAULT_INTERVAL;
		this.intervalUnit = (intervalUnit != null) ? intervalUnit : DEFAULT_INTERVAL_UNIT;
	}

	/**
	 * Same as {@link #StorageWatcher(List, Long, TimeUnit)} with the interval settings: {@link #DEFAULT_INTERVAL} and {@link #DEFAULT_INTERVAL_UNIT}.
	 * @param components Components to watch
	 */
	StorageWatcher(List<StorageWatcherComponent> components) {
		this(components, DEFAULT_INTERVAL, DEFAULT_INTERVAL_UNIT);
	}

	@SuppressWarnings("BusyWait")
	public void run() {
		if (components == null || components.isEmpty()) {
			return;
		}

		try {
			while (true) {
				for (StorageWatcherComponent component : components) {
					WatchKey wk;
					try {
						wk = component.watcher.poll(50, TimeUnit.MILLISECONDS);
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
							component.properties.onChanged.onChanged(component.properties.directory.toString(), filename.toString());
						}
						boolean valid = wk.reset();
						if (!valid) { break; }
					}
				}
				Thread.sleep(intervalUnit.toMillis(interval));
			}
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String stacktrace = errors.toString();
			Logger.getGlobal().severe(e.toString() + ". " + stacktrace);
		}
	}



}
