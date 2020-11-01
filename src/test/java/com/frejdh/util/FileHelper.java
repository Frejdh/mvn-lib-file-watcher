package com.frejdh.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileHelper {


	public static void createFile(String filename) throws Exception {
		File myObj = new File(filename);
		if (!myObj.createNewFile()) {
			throw new IOException("File already exists");
		}
	}

	public static void writeToExistingFile(String filename, String content) throws Exception {
		FileWriter myWriter = new FileWriter(filename);
		myWriter.write(content);
		myWriter.close();
	}

	public static void deleteFile(String filename) throws Exception {
		File myObj = new File(filename);
		if (!myObj.createNewFile()) {
			throw new IOException("File already exists");
		}
	}
}
