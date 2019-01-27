package com.cooltrickshome;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Contains methods used for performing conversions
 * @author csanuragjain
 * https://cooltrickshome.blogspot.com
 */
public class Utility {

	static String certificateName = "CERT";
	static String pathSeparator=File.pathSeparator;
	static String apkName = "";
	static File apkPath = null;
	static int memoryAllocated=1500;
	static String heapArg="-Xmx";
	static String memUnit="m";
	static List<File> dexFileList = new ArrayList<>();
	static List<File> jarFileList = new ArrayList<>();
	static List<Process> processList = new ArrayList<>();

	// Delete file windows rmdir /s /q folder
	
	public static void retrieveAllocatedMemory()
	{
		try {
			Properties prop = new Properties();
			InputStream input = new FileInputStream(getSettingsFile());
			prop.load(input);
			memoryAllocated=Integer.parseInt(prop.getProperty("memoryAllocated"));
			memUnit=prop.getProperty("memoryAllocatedUnit");
			input.close();
		} catch (Exception e) {
			
		}
	}

	/**
	 * Jar sign creates CERT SF & RSA but some apk have different name like
	 * ABC.SF This module will rename the CERT.SF & CERT.RSA to the new
	 * certificate name
	 * 
	 * @param newAPKPath
	 *            Path of the modified apk
	 */
	public static void repackCert(String newAPKPath) {

		try {
			ZipFile zipFile = new ZipFile(newAPKPath);
			zipFile.extractFile("META-INF" + File.separator + "CERT.RSA",
					getProjectPath());
			zipFile.extractFile("META-INF" + File.separator + "CERT.SF",
					getProjectPath());
			zipFile.removeFile("META-INF" + File.separator + "CERT.SF");
			zipFile.removeFile("META-INF" + File.separator + "CERT.RSA");
			File rsaFile = new File(getProjectPath() + File.separator
					+ "META-INF" + File.separator + "CERT.RSA");
			File sfFile = new File(getProjectPath() + File.separator
					+ "META-INF" + File.separator + "CERT.SF");
			File newCertFolder = new File(getProjectPath() + File.separator
					+ "META-INF");
			rsaFile.renameTo(new File(getProjectPath() + File.separator
					+ "META-INF" + File.separator + certificateName + ".RSA"));
			sfFile.renameTo(new File(getProjectPath() + File.separator
					+ "META-INF" + File.separator + certificateName + ".SF"));
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			zipFile.addFolder(newCertFolder, parameters);

		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract apk without decoding
	 * 
	 * @param apkPath
	 *            Path of apk
	 * @param outputDirectory
	 *            Output directory for extraction
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void extractAPK(String apkPath, String outputDirectory)
			throws InterruptedException, IOException {
		// String[] commands =
		// {"cmd","/c","start","/B","java","-jar",getAPKToolLibraryPath(),"d",apkPath,"-rso",outputDirectory};
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-jar", getAPKToolLibraryPath(), "d",
				apkPath, "-rfso", outputDirectory };
		runProgram(commands, getProjectPath());
	}

	/**
	 * Decodes the apk and puts the content
	 * 
	 * @param apkPath
	 *            Path of apk
	 * @param outputDirectory
	 *            Path where extracted file will be kept
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void extractSmaliUsingAPKTool(String apkPath,
			String outputDirectory) throws InterruptedException, IOException {
		// String[] commands =
		// {"cmd","/c","start","/B","java","-jar",getAPKToolLibraryPath(),"d",apkPath,"-fo",outputDirectory};
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-jar", getAPKToolLibraryPath(), "d",
				apkPath, "-f","-o", outputDirectory };
		runProgram(commands, getProjectPath());
	}

	/**
	 * Rewrites the class dex file into the new provided apk file and removes
	 * the certificate
	 * 
	 * @param newAPKPath
	 *            Path of the new modified apk
	 * @throws ZipException
	 */
	public static void rewriteDexInAPK(String newAPKPath) throws ZipException {
		try {
			ZipFile zipFile = new ZipFile(newAPKPath);
			List<String> classDex = getClassDexFiles(Utility.getApkSource());
			ZipParameters parameters = new ZipParameters();
			parameters.setIncludeRootFolder(false);
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			for (String s : classDex) {
				zipFile.removeFile(s);
				zipFile.addFile(new File(Utility.getModifiedDex()
						+ File.separator + s), parameters);
			}
			List<String> certList = getCertList(Utility.getApkSource()
					+ File.separator + "original");
			for (String s : certList) {
				zipFile.removeFile("META-INF" + File.separator + s);
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Work in progress
	 * 
	 * @param smaliFilePath
	 * @throws IOException
	 */
	public static void retrieveModuleFromSmali(File smaliFilePath)
			throws IOException {
		FileReader fr = new FileReader(smaliFilePath);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			line = br.readLine();
		}
	}

	/**
	 * Retrieves the classes dex files from the input directory
	 * 
	 * @param apkFolder
	 *            Folder from which dex files will be searched
	 * @return List containing the name of dex files
	 */
	public static List<String> getClassDexFiles(String apkFolder) {
		List<String> classDex = new ArrayList<>();
		File f = new File(apkFolder);
		File[] listFile = f.listFiles();
		for (File f2 : listFile) {
			if (f2.getName().contains("dex")) {
				classDex.add(f2.getName());
			}
		}
		return classDex;
	}

	/**
	 * Retrieves the certificate names from the extracted apk folder
	 * 
	 * @param apkFolder
	 *            Folder containing the apk data
	 * @return Returns a List containing name of certificates within the
	 *         meta-inf
	 */
	public static List<String> getCertList(String apkFolder) {
		List<String> certName = new ArrayList<>();
		File f = new File(apkFolder + File.separator + "META-INF");
		File[] listFile = f.listFiles();
		for (File f2 : listFile) {
			if (f2.getName().contains("RSA") || f2.getName().contains("SF")) {
				String fileName = f2.getName();
				certificateName = fileName.substring(0, fileName.indexOf("."))
						.trim();
				certName.add(fileName);
			}
		}
		return certName;
	}

	/**
	 * It runs the program from the provided working directory
	 * 
	 * @param program
	 *            Program which would be run
	 * @param workingDir
	 *            Directory from which program would be run
	 * @return returns 0 if success else 1
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static int runProgram(String[] program, String workingDir)
			throws InterruptedException, IOException {
		int errorFound = 0;
		String commandRun="";
		for(String command:program){
			commandRun+=command+" ";
		}
		ConsoleViewer.setText(commandRun);
		Process proc = Runtime.getRuntime().exec(program, null,
				new File(workingDir));
		processList.add(proc);
		ProcessHandler inputStream = new ProcessHandler(proc.getInputStream(),
				"INPUT");
		ProcessHandler errorStream = new ProcessHandler(proc.getErrorStream(),
				"ERROR");
		/* start the stream threads */
		inputStream.start();
		errorStream.start();

		if (0 == proc.waitFor()) {
			ConsoleViewer.setText("Process completed successfully");
		} else {
			ConsoleViewer
					.setText("Encountered errors/warnings while running this program");
		}
		processList.remove(proc);
		return errorFound;
	}

	/**
	 * Searches & Copies the classes.dex files from apkSource Folder to dexFile
	 * folder. Since apk may have multiple dex files so would retrieve all and
	 * store in dexFileList
	 * 
	 * @param apkSource
	 *            Folder from where dex file would be copied
	 * @param dexFolder
	 *            Folder where dex files from source folder would be copied
	 * @throws IOException
	 */
	public static void searchAndCopyDexFile(String apkSource, String dexFolder)
			throws IOException {
		File source = new File(apkSource);
		File[] f = source.listFiles();
		for (File f1 : f) {
			if (f1.getName().contains(".dex")) {
				try {
					File dexFile = new File(dexFolder + File.separator
							+ f1.getName());
					FileUtils.copyFile(f1, dexFile);
					dexFileList.add(dexFile);
				} catch (IOException e) {
					ConsoleViewer.setText("Unable to copy dex file. "
							+ e.getMessage());
					throw new IOException();
				}
			}
		}
	}

	/**
	 * Extra function Converts dex file to Jar
	 * 
	 * @param dexFile
	 *            Input dex file to be converted to Jar
	 * @param outputFile
	 *            Resulting jar file
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JadxException
	 */
	public static void changeDex2Jar(File dexFile, File outputFile)
			throws InterruptedException, IOException {
		// Dex2jarCmd.main(new
		// String[]{"--force","--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "dex2Jar", dexFile.getAbsolutePath(),
				outputFile.getAbsolutePath() };
		runProgram(commands, getHelperPath());
	}

	/**
	 * Converts dex format file to smali
	 * 
	 * @param dexFile
	 *            input dex file
	 * @param outputFile
	 *            output smali file
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void changeDex2Smali(File dexFile, File outputFile)
			throws InterruptedException, IOException {
		// BaksmaliCmd.main(new
		// String[]{"--force","--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "dex2Smali",
				dexFile.getAbsolutePath(), outputFile.getAbsolutePath() };
		runProgram(commands, getHelperPath());
	}

	/**
	 * Change smali format file to dex
	 * 
	 * @param smaliFile
	 *            input smali file
	 * @param outputFile
	 *            output dex file
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void changeSmali2Dex(File smaliFile, File outputFile)
			throws InterruptedException, IOException {
		// SmaliCmd.main(new
		// String[]{"--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "smali2Dex",
				smaliFile.getAbsolutePath(), outputFile.getAbsolutePath() };
		runProgram(commands, getHelperPath());
	}

	/**
	 * Decompiles the jar file to source java into the outputSourceDirectory
	 * 
	 * @param jarPath
	 *            Path of the dex file to be decompiled
	 * @param outputSourceDirectory
	 *            Output directory where decompiled files would be kept
	 * @throws Exception
	 */
	public static void decompileJar2Java(File jarPath,
			String outputSourceDirectory) throws Exception {
		/*
		 * JadxDecompiler jadx = new JadxDecompiler(); jadx.setOutputDir(new
		 * File(outputSourceDirectory)); jadx.loadFile(jarPath); jadx.save(); if
		 * (jadx.getErrorsCount() != 0) { jadx.printErrorsReport(); } else {
		 * ConsoleViewer.setText("Completed"); }
		 */
		String[] commands = { "java",heapArg+memoryAllocated+memUnit,"-cp",
				"."+pathSeparator + getJadLibraryPath() + File.separator + "*",
				getJadHelperClassName(), "jadx", jarPath.getAbsolutePath(),
				outputSourceDirectory };
		runProgram(commands, getHelperPath());
	}

	public static void destroyDanglingProcess(){
    	for(Process p:processList){
    		try{p.destroyForcibly();} catch(Exception e){e.printStackTrace();}
    	}
	}
	
	/**
	 * Sleeps and free up memory
	 * 
	 * @param sec
	 */
	public static void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
			System.gc();
		} catch (Exception e) {

		}
	}

	/**
	 * Converts jar to dex
	 * 
	 * @param modJarPath
	 *            Path containing jar
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void changeJar2Dex(String modJarPath)
			throws InterruptedException, IOException {
		File source = new File(modJarPath);
		File[] f = source.listFiles();
		for (File f1 : f) {
			if (f1.getName().contains(".jar")) {
				jar2Dex(f1.getAbsolutePath(), Utility.getModifiedDex()
						+ File.separator + f1.getName().replace(".jar", ".dex"));
				sleep(1);
			}
		}
	}

	/**
	 * Sign passed apk with new name
	 * 
	 * @param input
	 *            original apk
	 * @param output
	 *            signed apk new name
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void signApk(String input, String output)
			throws InterruptedException, IOException {
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "signApk", input, output };
		runProgram(commands, getHelperPath());
	}

	/**
	 * Converts jar to dex format
	 * 
	 * @param jarPath
	 *            input jar file path
	 * @param outputPath
	 *            output file path
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void jar2Dex(String jarPath, String outputPath)
			throws InterruptedException, IOException {
		// Jar2Dex.main(new String[]{"--force","--output",outputPath,jarPath});
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "jar2Dex", jarPath, outputPath };
		runProgram(commands, getHelperPath());
	}

	/**
	 * Converts the passes class to dex format
	 * 
	 * @param classPath
	 *            class file
	 * @param dexPath
	 *            output file path
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void changeClass2Dex(String classPath, String dexPath)
			throws InterruptedException, IOException {
		// Jar2Dex.main(new String[]{"--force","--output",dexPath,classPath});
		String[] commands = { "java",heapArg+memoryAllocated+memUnit, "-cp",
				"."+pathSeparator + getDexLibraryPath() + File.separator + "*",
				getDexHelperClassName(), "class2Dex", classPath, dexPath };
		runProgram(commands, getHelperPath());
	}

	/**
	 * If one class has several subclass then this module will find all
	 * subclass.
	 * 
	 * @param firstPart
	 *            First name of class before extension
	 * @param lastPart
	 *            extension with .
	 * @param directorySearch
	 *            Directory to search class
	 * @return
	 */
	public static List<String> searchInternalClass(String firstPart,
			String lastPart, File directorySearch) {
		if (firstPart.contains("$")) {
			firstPart = firstPart.substring(0, firstPart.indexOf("$"));
		}
		List<String> classesFoundList = new ArrayList<>();
		String[] classList = directorySearch.list();
		classesFoundList.add(firstPart + lastPart);
		for (String c : classList) {
			if (c.startsWith(firstPart + "$") && c.endsWith(lastPart)) {
				classesFoundList.add(c);
			}
		}
		return classesFoundList;
	}

	/**
	 * If one class has several subclass then this module will find all
	 * subclass. In this case it will find smali
	 * 
	 * @param firstPart
	 *            First name of smali before extension
	 * @param lastPart
	 *            extension with .
	 * @param directorySearch
	 *            Directory to search smali
	 * @return
	 */
	public static List<File> searchInternalSmali(String firstPart,
			String lastPart, File directorySearch) {
		if (firstPart.contains("$")) {
			firstPart = firstPart.substring(0, firstPart.indexOf("$"));
		}
		List<File> classesFoundList = new ArrayList<>();
		classesFoundList.add(new File(directorySearch + File.separator
				+ firstPart + lastPart));
		File[] classList = directorySearch.listFiles();
		for (File c : classList) {
			if (c.getName().startsWith(firstPart + "$")
					&& c.getName().endsWith(lastPart)) {
				classesFoundList.add(c);
			}
		}
		return classesFoundList;
	}

	/**
	 * Writes the filecontent to the provided file
	 * 
	 * @param newFilePath
	 *            File on which content would be written
	 * @param contentToBeWritten
	 *            Content to be written to file
	 * @throws IOException
	 */
	public static void writeFile(String newFilePath, String contentToBeWritten)
			throws IOException {
		File f = new File(newFilePath);
		f.getParentFile().mkdirs();
		FileWriter fw = new FileWriter(newFilePath);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(contentToBeWritten);
		bw.flush();
		bw.close();
		fw.close();
	}

	/**
	 * Compiles the java file at provided filepath using libraryDir as classpath
	 * Example filepath
	 * classes.dex\com\blogspot\cooltrickshome\simpleproject\MainActivity.java
	 * Module will remove classes.dex\ from input
	 * 
	 * @param file
	 *            File path relative to javaCode folder
	 * @param libraryDir
	 *            Defines the directory for classpath
	 * @param compileDir
	 *            Defines the File directory from which compilation would happen
	 * @throws Exception
	 */
	public static void compileFile(String file, String libraryDir,
			File compileDir) throws Exception {
		String dexFileName = "", jarFilePath = "";
		// file=file.replaceFirst(File.separator+File.separator, "");
		dexFileName = file.substring(0, file.indexOf(File.separator));
		jarFilePath = Utility.getModifiedJar() + File.separator + "*";
		file = file.substring(file.indexOf("dex") + 4, file.length());
		// String[] commands =
		// {"cmd","/c","start","cmd","/k","javac","-cp","."+pathSeparator+jarFilePath+";"+libraryDir+File.separator+"*",file};
		String[] commands = { "javac", "-cp",
				"."+pathSeparator + jarFilePath + ";" + libraryDir + File.separator + "*",
				file };
		String compileCommand = "";
		for (String command : commands) {
			compileCommand += command + " ";
		}
		ConsoleViewer.setText("Compiling: " + compileCommand);
		// Runtime.getRuntime().exec(commands,null,compileDir);
		runProgram(commands, compileDir.getAbsolutePath());
	}

	/**
	 * Updates the jar file with the new class file changes
	 * 
	 * @throws Exception
	 */
	public static void repackageJar() throws Exception {
		File classFiles = new File(getClassFile());
		File[] classFolders = classFiles.listFiles();
		for (File f : classFolders) {
			String modifiedJarName = getModifiedJar() + File.separator
					+ f.getName().replace(".dex", ".jar");
			ConsoleViewer
					.setText("Started packaging jar at " + modifiedJarName);
			// String[] commands =
			// {"cmd","/c","start","/B","jar","vfu",modifiedJarName,"*"};
			String[] commands = { "jar", "vfu", modifiedJarName, "*" };
			runProgram(commands, f.getAbsolutePath());
		}
		ConsoleViewer.setText("Jar packaging complete");
	}

	/**
	 * Used to open the url passed
	 * 
	 * @param uri
	 *            URL to be opened in browser
	 */
	public static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
				JFrame f = new JFrame("URL to open");
				JEditorPane ed = new JEditorPane();
				ed.setFont(font);
				ed.setText("Java is not able to launch links on your computer.\nRequest you to kindly open below link manually \n"
						+ uri.toString());
				f.add(ed);
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				f.setSize(500, 200);
				f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height
						/ 2 - f.getSize().height / 2);
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		} else {
			Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
			JFrame f = new JFrame("URL to open");
			JEditorPane e = new JEditorPane();
			e.setFont(font);
			e.setText("Java is not able to launch links on your computer.\nRequest you to kindly open below link manually \n"
					+ uri.toString());
			f.add(e);
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			f.setSize(500, 200);
			f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2
					- f.getSize().height / 2);
			f.setVisible(true);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	/**
	 * Contains the base path where everything lies
	 * 
	 * @return
	 */
	public static String getBasePath() {
		File f = new File("");
		return f.getAbsolutePath();
	}
	
	/**
	 * Contains the settings.txt having memory information
	 * 
	 * @return
	 */
	public static String getSettingsFile() {
		return getBasePath()+File.separator+"settings.txt";
	}

	/**
	 * Returns the project path
	 * 
	 * @return
	 */
	public static String getProjectPath() {
		return getBasePath() + File.separator + "Projects" + File.separator
				+ apkName;
	}

	/**
	 * Contain library to be used by user while compilation
	 * 
	 * @return
	 */
	public static String getLibraryPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "userLibrary";
	}

	/**
	 * Contains jad library
	 * 
	 * @return
	 */
	public static String getJadLibraryPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "jadx";
	}

	/**
	 * Contains the software icon
	 * 
	 * @return
	 */
	public static String getIconPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "icon" + File.separator + "icon.png";
	}

	/**
	 * Contains the dex library for conversion
	 * 
	 * @return
	 */
	public static String getDexLibraryPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "dex";
	}

	/**
	 * Program for utilizing jad
	 * 
	 * @return
	 */
	public static String getJadHelperClassName() {
		return "com.cooltrickshome.helper.RunProgramJad";
	}

	/**
	 * Program for utilizing dex library
	 * 
	 * @return
	 */
	public static String getDexHelperClassName() {
		return "com.cooltrickshome.helper.RunProgramDex";
	}

	/**
	 * Path for apktool.jar
	 * 
	 * @return
	 */
	public static String getAPKToolLibraryPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "apktool" + File.separator + "apktool.jar";
	}

	/**
	 * Contains the dex from original apk
	 * 
	 * @return
	 */
	public static String getDexPath() {
		return getProjectPath() + File.separator + "dexFile";
	}

	/**
	 * Contains the jar from original apk
	 * 
	 * @return
	 */
	public static String getDex2JarPath() {
		return getProjectPath() + File.separator + "dex2Jar";
	}

	/**
	 * Contains the compiled class files of apk
	 * 
	 * @return
	 */
	public static String getClassFile() {
		return getProjectPath() + File.separator + "classFile";
	}

	/**
	 * Contains the source code of apk
	 * 
	 * @return
	 */
	public static String getJavaCodeFolder() {
		return getProjectPath() + File.separator + "javaCode";
	}

	/**
	 * Contains apk source with no decoding, used to extract dex
	 * 
	 * @return
	 */
	public static String getApkSource() {
		return getProjectPath() + File.separator + "apkSource";
	}

	/**
	 * Contains the decoded apk source
	 * 
	 * @return
	 */
	public static String getApkToolSource() {
		return getProjectPath() + File.separator + "apkToolSource";
	}

	/**
	 * Contains the modified jar files
	 * 
	 * @return
	 */
	public static String getModifiedJar() {
		return getProjectPath() + File.separator + "modifiedJar";
	}

	/**
	 * Contains the converted dex files
	 * 
	 * @return
	 */
	public static String getModifiedDex() {
		return getProjectPath() + File.separator + "modifiedDex";
	}

	/**
	 * Used when smali editing is used
	 * 
	 * @return
	 */
	public static String getSmaliPath() {
		return getProjectPath() + File.separator + "smali";
	}

	/**
	 * Path of helper class containing conversion logic
	 * 
	 * @return
	 */
	public static String getHelperPath() {
		return getBasePath() + File.separator + "APKRepatcher_lib" + File.separator
				+ "helper";
	}

	/**
	 * Makes the folder required by project
	 */
	public static void prepareProject() {
		dexFileList = new ArrayList<>();
		jarFileList = new ArrayList<>();
		File fi = new File(getProjectPath());
		fi.mkdirs();
		File f = new File(getProjectPath() + File.separator + "dexFile");
		f.mkdirs();
		File f2 = new File(getProjectPath() + File.separator + "javaCode");
		f2.mkdirs();
		File f3 = new File(getProjectPath() + File.separator + "dex2jar");
		f3.mkdirs();
		File f4 = new File(getProjectPath() + File.separator + "classFile");
		f4.mkdirs();
		File f5 = new File(getProjectPath() + File.separator + "modifiedJar");
		f5.mkdirs();
		File f6 = new File(getProjectPath() + File.separator + "modifiedDex");
		f6.mkdirs();
		File f7 = new File(getSmaliPath());
		f7.mkdirs();
		APKRepatcher.tabPane.removeAll();
		APKRepatcher.openedTabs.clear();
	}

	/**
	 * Delete the extracted apk folder
	 */
	public static void deleteProjectFile() {
		File f = new File(getApkSource());
		f.delete();
	}

}
