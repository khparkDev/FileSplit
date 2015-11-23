package com.khpark;

import static com.khpark.Logger.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;

public class WorkerThread implements Callable<Boolean> {
	private String filename;
	private int fileNum;
	private long startPosition;
	private long limitSize;
	private long bufferSize;
	private FileChannel cin;
	private ByteBuffer byteBuffer;

	@SuppressWarnings("resource")
	public WorkerThread(String filename, int fileNum, long startPosition, long limitSize, long bufferSize) throws FileNotFoundException {
		this.filename = filename;
		this.fileNum = fileNum;
		this.startPosition = startPosition;
		this.limitSize = limitSize;
		this.bufferSize = bufferSize;
		cin = new FileInputStream(new File(filename)).getChannel();
		byteBuffer = ByteBuffer.allocateDirect((int) bufferSize);
	}

	@Override
	public Boolean call() {
		boolean result = true;
		FileOutputStream fos = null;
		FileChannel fout = null;

		try {
			fos = new FileOutputStream(filename + "_" + String.valueOf(fileNum));
			fout = fos.getChannel();
			long readSize = 0;
			long position = startPosition;

			while (readSize < limitSize) {
				cin.read(byteBuffer, position);
				byteBuffer.flip();

				if (limitSize - readSize < bufferSize) {
					byteBuffer.limit((int) (limitSize - readSize));
				}

				fout.write(byteBuffer);
				byteBuffer.clear();

				readSize += bufferSize;
				position += bufferSize;
			}

			fout.close();
			fos.close();
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			try {
				fout.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		print(fileNum + " file splited " + (result ? " successfully " : " abnormally") + "\n");

		return result;
	}
}