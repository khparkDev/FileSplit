package com.khpark;

public class Main {

	/**
	 * new TextFileSplit(절대경로포함-파일명)
	 * new TextFileSplit(절대경로포함-파일명, 분할할 파일 개수)
	 * new TextFileSplit(절대경로포함-파일명, 분할할 파일 개수, 버퍼크기)
	 * 
	 * @Author khpark
	 */
	public static void main(String... args) throws Exception {
		String filename = "C:\\Users\\khpark\\git\\FileSplit\\resources\\http2-documentation.txt";
		new TextFileSplit(filename).executeSplitFile();
		//new TextFileSplit(filename, 20).executeSplitFile();
		//new TextFileSplit(filename, 5, 2000).executeSplitFile();
	}
}
