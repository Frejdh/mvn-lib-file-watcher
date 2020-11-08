package com.frejdh.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileHelper {
	private static final List<String> files = new ArrayList<>();
	private static final String FILENAME_BASE = "test_file_%s.txt";
	private static int filenameCounter = 1;
	private static final String classpath = FileHelper.class.getClassLoader().getResource("").getPath();

	public static String nextFilename() {
		return String.format(FILENAME_BASE, filenameCounter++);
	}

	public static void cleanup() {
		for (String file : files) {
			new File(classpath + file).delete();
		}
		files.clear();
	}

	public static void createFile(String filename) throws Exception {
		File file = new File(classpath + filename);
		if (!file.createNewFile()) {
			throw new IOException("File already exists");
		}
		else {
			files.add(filename);
		}
	}

	public static void writeToExistingFile(String filename, String content) throws Exception {
		FileWriter myWriter = new FileWriter(classpath + filename);
		myWriter.write(content);
		myWriter.close();
		files.add(filename);
	}

	public static void deleteFile(String filename) throws Exception {
		new File(classpath + filename).delete();
		files.removeIf(file -> file.equals(filename));
	}
}
