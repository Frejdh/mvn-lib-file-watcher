package com.frejdh.util;

import com.frejdh.util.watcher.StorageWatcher;
import com.frejdh.util.watcher.StorageWatcherBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.StandardWatchEventKinds;
import java.util.logging.Logger;

public class WatcherTests {

	private Logger logger = Logger.getLogger("WatcherTests");

	private boolean flag1;
	private boolean flag2;
	private boolean flag3;

	@Before
	public void reset() {
		flag1 = flag2 = flag3 = false;
	}

	@Test
	public void watchOneFileCreation() throws Exception {
		String filename = "test1.txt";

		StorageWatcher watcher = new StorageWatcherBuilder()
				.specifyEvent(StandardWatchEventKinds.ENTRY_CREATE)
				.watchFile(filename)
				.onChanged(() -> {
					flag1 = true;
					System.out.println("YEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEES");
				})
				.build();
		watcher.start();
		FileHelper.createFile(filename);
		Thread.sleep(2000);
		Assert.assertTrue("Flag 1 not set", flag1);
	}
}
