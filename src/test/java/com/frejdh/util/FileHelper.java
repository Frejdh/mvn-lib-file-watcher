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

	public static String nextFilename() {
		return String.format(FILENAME_BASE, filenameCounter++);
	}

	public static void cleanup() {
		for (String file : files) {
			new File(file).delete();
		}
		files.clear();
	}

	public static void createFile(String filename) throws Exception {
		File myObj = new File(filename);
		if (!myObj.createNewFile()) {
			throw new IOException("File already exists");
		}
		else {
			files.add(filename);
		}
	}

	public static void writeToExistingFile(String filename, String content) throws Exception {
		FileWriter myWriter = new FileWriter(filename);
		myWriter.write(content);
		myWriter.close();
		files.add(filename);
	}

	public static void deleteFile(String filename) throws Exception {
		new File(filename).delete();
		files.removeIf(file -> file.equals(filename));
	}
}
