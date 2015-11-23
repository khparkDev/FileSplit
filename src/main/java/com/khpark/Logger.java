package com.khpark;

public class Logger {
	private static final String DECORATOR = "=";

	public static void print(String str) {
		System.out.println(str);
	}

	public static void printLine(int length) {
		for (int i = 0; i < length; i++) {
			System.out.print(DECORATOR);
		}

		System.out.println();
	}
}
