package com.khpark;

import static com.khpark.Logger.print;
import static com.khpark.Logger.printLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextFileSplit {
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private static final int MB = 1024 * 1000;
	private int splitFileCount;
	private String filename;
	private long srcFileSize;
	private long oneFileSize;
	private long remainedFileSize;
	private int bufferSize;
	private List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
	private List<Boolean> executeResultList = new ArrayList<Boolean>();

	public TextFileSplit() throws Exception {
		throw new Exception("Filename must be not null!");
	}

	public TextFileSplit(String filename) throws Exception {
		this(filename, -1, -1);
	}

	public TextFileSplit(String filename, int splitFileCount) throws Exception {
		this(filename, splitFileCount, -1);
	}

	public TextFileSplit(String filename, int splitFileCount, int bufferSize) throws Exception {
		this.filename = filename;
		this.splitFileCount = splitFileCount;
		this.bufferSize = bufferSize;
		init();
	}

	public void executeSplitFile() throws IOException, InterruptedException, ExecutionException {
		int startPosition = 0;
		boolean executeResult = true;
		String resultMsg = "";

		for (int i = 0; i < splitFileCount; i++) {
			long limitSize = (i < splitFileCount - 1) ? oneFileSize : remainedFileSize;
			Future<Boolean> result = executorService.submit(new WorkerThread(filename, i + 1, startPosition, limitSize, bufferSize));
			futureList.add(result);
			startPosition += oneFileSize;
		}

		for (int j = 0; j < splitFileCount; j++) {
			executeResultList.add(futureList.get(j).get());
		}

		for (int j = 0; j < splitFileCount; j++) {

			if (!executeResultList.get(j)) {
				executeResult = false;
				break;
			}
		}

		resultMsg = (executeResult) ? "all thread successfully executed." : "some thread abmormally executed.";

		printLine(40);
		print(resultMsg);
		printLine(40);

		executorService.shutdownNow();
	}

	@SuppressWarnings("resource")
	private void init() throws Exception {
		FileChannel cin = new FileInputStream(new File(filename)).getChannel();
		srcFileSize = cin.size();
		adjustBufferSize();
		adjustSplitCount();
		oneFileSize = srcFileSize / (splitFileCount - 1);
		remainedFileSize = srcFileSize - (oneFileSize * (splitFileCount - 1));
		cin.close();
		printInitFileInfo();
	}

	private void adjustSplitCount() {
		if (splitFileCount > 0) {
			return;
		}

		if (srcFileSize < MB * 100) {
			splitFileCount = 3;
		} else if (srcFileSize >= MB * 100 && srcFileSize < MB * 500) {
			splitFileCount = 5;
		} else {
			splitFileCount = 10;
		}
	}

	private void adjustBufferSize() throws Exception {
		if (bufferSize > 0) {
			return;
		}

		if (srcFileSize < MB) {
			throw new Exception("Minimum file size must be up to 1MB.");
		}

		bufferSize = (srcFileSize < MB * 100) ? MB : MB * 10;
	}

	private void printInitFileInfo() {
		printLine(40);
		print("source File Size : " + srcFileSize + "(" + (oneFileSize * (splitFileCount - 1) + remainedFileSize) + ")");
		print("One File Size : " + oneFileSize + ", Remained File Size : " + remainedFileSize);
		print(", split count : " + splitFileCount + ", buffer size : " + bufferSize);
		printLine(40);
	}
}