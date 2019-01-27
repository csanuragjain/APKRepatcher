package com.cooltrickshome;

/**
 * Helps make changes to console in GUI
 * @author csanuragjain
 * https://cooltrickshome.blogspot.com
 */
public class ConsoleViewer {

	/**
	 * Clears the console
	 */
	public static void cleanConsole() {
		APKRepatcher.consoleArea.setText("");
	}

	/**
	 * Writes on console
	 * 
	 * @param text
	 *            Text to be written
	 */
	public static void setText(String text) {
		APKRepatcher.consoleArea.append(text + "\n");
		APKRepatcher.consoleArea.setCaretPosition(APKRepatcher.consoleArea
				.getDocument().getLength());
	}

}
