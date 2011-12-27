package com.willmeyer.mmxcode;

public class Debug {

	public static boolean ON = true;
	public static boolean EXEC_OUTPUT = false; // set this manually
	
	public static void println(String msg) {
		if (ON) System.out.println(msg);
	}
	
	public static void println() {
		Debug.println("");
	}
}
