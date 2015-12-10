package com.khpark;

/**
 * The type Logger.
 */
public class Logger {
	private static final String DECORATOR = "=";

	/**
	 * Print.
	 *
	 * @param str the str
	 */
	public static void print(String str) {
		System.out.println(str);
	}

	/**
	 * Print line.
	 *
	 * @param length the length
	 */
	public static void printLine(int length) {
		for (int i = 0; i < length; i++) {
			System.out.print(DECORATOR);
		}

		System.out.println();
	}
}
