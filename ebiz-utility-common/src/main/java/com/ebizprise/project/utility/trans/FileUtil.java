package com.ebizprise.project.utility.trans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author thiago
 *
 */
public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static enum sizeType {
		KB, MB, GB
	}

	public static final double kilobyte = Math.pow(2, 10);
	public static final double megabyte = Math.pow(2, 20);
	public static final double gigabyte = Math.pow(2, 30);

	/**
	 * Generate a file with name and size defined by @param size
	 * 
	 * @param size
	 *            Size of files in bytes
	 */
	public static File generate(final long size) {
		return generate("", size);
	}

	/**
	 * Generate a file into the path specified. The file name is its size number.
	 * 
	 * @param path
	 * @param size
	 *            in bytes
	 */
	public static File generate(String path, long size) {
		return generate(path, size, Long.toString(size));
	}

	/**
	 * Generate a file into path specified.
	 * 
	 * @param path
	 * @param size
	 * @param sizeType
	 */
	public static File generate(String path, long size, FileUtil.sizeType sizeType) {
		size = getSize(size, sizeType);
		return generate("", size);
	}

	/**
	 * Generate a file into the path specified, with the name provided.
	 * 
	 * @param path
	 * @param size
	 * @param fileName
	 */
	public static File generate(final String path, final long size, final String fileName) {
		try {

			PrintWriter file = new PrintWriter(new BufferedWriter(new FileWriter(path + fileName + ".out")));
			for (long i = 0; i < size; i++) {
				file.append('a');
			}
			file.close();

			Runnable th = new Runnable() {
				@Override
                public void run() {
					try {
						FileInputStream srcFile = new FileInputStream(path + fileName + ".out");
						String md5 = DigestUtils.md5Hex(srcFile);
						srcFile.close();

						PrintWriter md5File = new PrintWriter(
								new BufferedWriter(new FileWriter(path + fileName + ".md5")));
						md5File.append(md5);
						md5File.close();
					} catch (IOException x) {
						x.printStackTrace();
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			};
			Thread task = new Thread(th);
			task.start();
			return new File(path + fileName + ".out");

		} catch (IOException x) {
			x.printStackTrace();
		} catch (Exception x) {
			x.printStackTrace();
		}

		return null;
	}

	/**
	 * Return the size in bytes
	 * 
	 * @param fileSize
	 * @param sizeType
	 */
	public static long getSize(long fileSize, FileUtil.sizeType sizeType) {

		long size = 0;

		switch (sizeType) {
		case KB:
			size = fileSize * (long) kilobyte;
			break;
		case MB:
			size = fileSize * (long) megabyte;
			break;
		case GB:
			size = fileSize * (long) gigabyte;
			break;
		default:
			size = fileSize * (long) kilobyte;
			break;
		}

		return size;
	}

	/**
	 * Copy a file into another
	 * 
	 * @param from
	 * @param to
	 */
	public static void copyFile(String from, String to) throws IOException {
		int BUFF_SIZE = 100000;
		byte[] buffer = new byte[BUFF_SIZE];

		InputStream src = null;
		OutputStream dst = null;

		try {
			src = new FileInputStream(from);
			dst = new FileOutputStream(to);
			while (true) {
				int amountRead = src.read(buffer);
				if (amountRead == -1) {
					break;
				}
				dst.write(buffer, 0, amountRead);
			}
		} finally {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
		}
	}

	/**
	 * List recursively all files of a directory
	 * 
	 * @param path
	 */
	public static File[] listFiles(File path) {
		List<File> fileList = new ArrayList<File>();

		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					File[] tmp = listFiles(files[i]);
					for (int j = 0; j < tmp.length; j++) {
						fileList.add(tmp[j]);
					}
				} else {
					fileList.add(files[i]);
				}
			}
		} else {
			fileList.add(path);
		}

		return fileList.toArray(new File[0]);
	}

	/**
	 * List directories
	 *
	 * @param path
	 */
	public static File[] listDirs(File path) {
		List<File> fileList = new ArrayList<File>();

		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					fileList.add(files[i]);
				}
			}
		}

		return fileList.toArray(new File[0]);
	}
	/**
	 * 讀取Class Path底下的檔案
	 * 
	 * @param path
	 * @return
	 * @author adam.yeh
	 */
	public static String readFile(String path) {
		StringBuilder sb = new StringBuilder();
		InputStream is = FileUtil.class.getResourceAsStream(path);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));

		try {
			String line = buf.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
		} catch (IOException e) {
			logger.error("Read file error", e);
		} finally {
			try {
				buf.close();
				is.close();
			} catch (Exception e) {
				logger.error("Stream close error", e);
			}
		}

		return sb.toString();
	}

	/**
	 * 將檔案轉成 byte
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] getByteFromFile(String path) throws IOException {
		File file = new File(path);
		byte[] buffer = new byte[(int) file.length()];
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			if (is.read(buffer) == -1) {
				throw new IOException("EOF reached while trying to read the whole file");
			}
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		return buffer;
	}

	/**
	 * 將 byte 資料轉存為 file
	 * @param data
	 * @param filePath
	 * @return
	 */
	public static File getFileFromByte(byte[] data, String filePath) throws IOException {
		try (FileOutputStream stream = new FileOutputStream(filePath)) {
			stream.write(data);
		}
		return new File(filePath);
	}
}
