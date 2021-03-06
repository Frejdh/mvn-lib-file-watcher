package com.frejdh.util;

import com.frejdh.util.watcher.StorageWatcher;
import com.frejdh.util.watcher.StorageWatcherBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import static com.frejdh.util.FileHelper.CleanupAction;

public class WatcherTests {

	private final Logger logger = Logger.getLogger("WatcherTests");
	private StorageWatcher watcher;

	private boolean flagCreate;
	private boolean flagModify;
	private boolean flagDelete;

	private final long DEFAULT_SLEEP = 1500;

	@Before
	public void reset() {
		flagCreate = flagModify = flagDelete = false;
	}

	@After
	public void cleanup() throws Exception {
		FileHelper.cleanup();
		if (watcher != null) {
			watcher.stop();
		}
	}

	@Test
	public void watchOneFileCreation() throws Exception {
		String filename = FileHelper.nextFilename();

		watcher = new StorageWatcherBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
				.watchFile(filename)
				.onChanged((dir, file) -> {
					flagCreate = true;
					logger.info("Flag Create set");
				})
				.build();
		watcher.start();
		FileHelper.createFile(filename);
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Modify not set", flagCreate);
	}

	@Test
	public void nestedBuilderCreateAndModify() throws Exception {
		String filename = FileHelper.nextFilename();

		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
				.watchFile(filename)
				.onChanged((dir, file) -> {
					flagCreate = true;
					logger.info("Flag Create set");
				})
				.createNext()
				.specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
				.watchFile(filename)
				.onChanged((dir, file) -> {
					flagModify = true;
					logger.info("Flag Modify set");
				})
				.build();
		watcher.start();

		FileHelper.createFile(filename);
		FileHelper.writeToExistingFile(filename, "file_to_watch.txt of modification");
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Create not set", flagCreate);
		Assert.assertTrue("Flag Modify not set", flagModify);
	}

	@Test
	public void watchDirectoryForModifications() throws Exception {
		String filename = FileHelper.nextFilename();

		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
				.watchDirectory("")
				.onChanged((dir, file) -> {
					flagModify = true;
					logger.info("Flag Modify set");
				})
				.build();
		watcher.start();


		FileHelper.createFile(filename);
		FileHelper.writeToExistingFile(filename, "file_to_watch.txt of modification");
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Modify not set", flagModify);
	}

	@Test
	public void ignoresOtherFiles() throws Exception {
		String filename = FileHelper.nextFilename();

		AtomicInteger numberOfInvokes = new AtomicInteger();
		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
				.watchFile(filename)
				.onChanged((dir, file) -> {
					flagCreate = true;
					numberOfInvokes.getAndIncrement();
					logger.info("Flag Create set");
				})
				.build();
		watcher.start();


		FileHelper.createFile(filename);
		FileHelper.createFile(FileHelper.nextFilename());
		FileHelper.writeToExistingFile(filename, "file_to_watch.txt of modification");
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Modify not set", flagCreate);
		Assert.assertEquals("Unexpected amount of invokes", 1, numberOfInvokes.get());
	}

	@Test
	public void deleteFlagWorks() throws Exception {
		String filename = FileHelper.nextFilename();
		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_DELETE)
				.watchDirectory("")
				.watchFile(filename)
				.onChanged((directory, file) -> {
					flagDelete = true;
					logger.info(String.format("Flag Delete set for %s, %s", directory, file));
				})
				.build();
		watcher.start();

		FileHelper.createFile(filename);
		FileHelper.deleteFile(filename);
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Delete not set", flagDelete);
	}

	@Test
	public void modifyExistingResourceFile() throws Exception {
		String filename = "persistence/file_to_watch.txt";
		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
				.watchFile(filename)
				.onChanged((directory, file) -> {
					flagModify = true;
					logger.info(String.format("Flag modify set for %s, %s", directory, file));
				})
				.build();
		watcher.start();

		FileHelper.writeToExistingFile(filename, "test of watcher", CleanupAction.EMPTY);
		Thread.sleep(DEFAULT_SLEEP);
		Assert.assertTrue("Flag Delete not set", flagModify);
	}

	@Test
	public void watcherCanBeStopped() throws Exception {
		String filename = "persistence/file_to_watch.txt";
		watcher = StorageWatcherBuilder.getBuilder()
				.interval(10, TimeUnit.MILLISECONDS)
				.specifyEvent(StandardWatchEventKinds.ENTRY_MODIFY)
				.watchFile(filename)
				.onChanged((directory, file) -> {
					flagModify = true;
					logger.info(String.format("Flag modify set for %s, %s", directory, file));
				})
				.build();

		watcher.start();
		Thread.sleep(100);
		Assert.assertTrue(watcher.getExecutionThread().isAlive());

		watcher.stop();
		Thread.sleep(100);
		Assert.assertFalse(watcher.getExecutionThread().isAlive());

		FileHelper.writeToExistingFile(filename, "test of watcher", CleanupAction.EMPTY);
		Assert.assertFalse("Flag Delete set, but shouldn't have been", flagModify);
	}
}
