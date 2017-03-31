package com.cooltrickshome;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Helps in preparation of new project
 * @author csanuragjain
 * https://cooltrickshome.blogspot.com
 */
public class PrepareProject {

	/**
	 * Helps in extracting an apk and put all its component in respective
	 * folders
	 * 
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static void start() throws InterruptedException, Exception {
		Utility.prepareProject();
		Utility.deleteProjectFile();
		ConsoleViewer.setText("Extracting apk content to "
				+ Utility.getApkSource());
		// UnzipUtility.unZipIt(Utility.apkPath.getAbsolutePath(),
		// Utility.getApkSource());
		Utility.extractAPK(Utility.apkPath.getAbsolutePath(),
				Utility.getApkSource());
		Utility.sleep(1);
		ConsoleViewer.setText("Apk extracted.");
		ConsoleViewer.setText("Extracting smali files using apkTool "
				+ Utility.getApkToolSource());
		Utility.extractSmaliUsingAPKTool(Utility.apkPath.getAbsolutePath(),
				Utility.getApkToolSource());
		ConsoleViewer.setText("Smali extracted using apktool.");
		ConsoleViewer.setText("Copying dex file for Analysis");
		Utility.searchAndCopyDexFile(Utility.getApkSource(),
				Utility.getDexPath());
		ConsoleViewer.setText("Dex file copied to " + Utility.getDexPath());
		Utility.sleep(1);
		ConsoleViewer
				.setText("Decompiling the dex to jar using dex2jar and thereafter to java source code using JAD");
		ConsoleViewer.setText("This process will take time");
		for (File dexFile : Utility.dexFileList) {
			File outputJarPath = new File(Utility.getDex2JarPath()
					+ File.separator
					+ dexFile.getName().replace(".dex", ".jar"));
			ConsoleViewer.setText("Dex2Jar: " + dexFile.getName() + " to "
					+ outputJarPath.getAbsolutePath());
			Utility.changeDex2Jar(dexFile, outputJarPath);
			Utility.sleep(1);
			File modifiedJarPath = new File(Utility.getModifiedJar()
					+ File.separator
					+ dexFile.getName().replace(".dex", ".jar"));
			FileUtils.copyFile(outputJarPath, modifiedJarPath);
			Utility.jarFileList.add(modifiedJarPath);

			String outputFolder = Utility.getJavaCodeFolder() + File.separator
					+ dexFile.getName();
			ConsoleViewer.setText("Dex2SourceCode: " + outputJarPath.getName()
					+ " to " + outputFolder);
			Utility.decompileJar2Java(dexFile, outputFolder);
			Utility.sleep(1);
		}
		ConsoleViewer.setText("Decompilation complete");
		ConsoleViewer.setText("Extract the class files from jar");
		for (File jarFile : Utility.jarFileList) {
			String destination = Utility.getClassFile() + File.separator
					+ jarFile.getName().replace(".jar", ".dex");
			File f = new File(destination);
			f.mkdirs();
			/*
			 * String destination=Utility.getClassFile(); String[] commands =
			 * {"cmd","/c","start","/B","jar","xf",jarFile.getAbsolutePath()};
			 * Utility.runProgram(commands,destination);
			 */
		}
		ConsoleViewer.setText("Extraction complete");
	}
}
