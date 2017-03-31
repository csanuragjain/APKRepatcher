package com.cooltrickshome;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * https://vyvaks.wordpress.com/2006/05/27/does-runtimeexec-hangs-in-java/
 */
public class ProcessHandler extends Thread {
	InputStream inputStream;
	String streamType;

	public ProcessHandler(InputStream inputStream, String streamType) {
		this.inputStream = inputStream;
		this.streamType = streamType;
	}

	public void run() {
		try {
			InputStreamReader inpStrd = new InputStreamReader(inputStream);
			BufferedReader buffRd = new BufferedReader(inpStrd);
			String line = null;
			while ((line = buffRd.readLine()) != null) {
				ConsoleViewer.setText(line);
			}
			buffRd.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}