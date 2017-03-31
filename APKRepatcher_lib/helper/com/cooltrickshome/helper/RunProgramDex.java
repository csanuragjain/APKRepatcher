package com.cooltrickshome.helper;

import jadx.api.JadxDecompiler;
import jadx.core.utils.exceptions.JadxException;

import java.io.File;

import com.googlecode.d2j.smali.BaksmaliCmd;
import com.googlecode.d2j.smali.SmaliCmd;
import com.googlecode.dex2jar.tools.ApkSign;
import com.googlecode.dex2jar.tools.Dex2jarCmd;
import com.googlecode.dex2jar.tools.Jar2Dex;

public class RunProgramDex {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args[0].equals("dex2Jar"))
		{
			changeDex2Jar(new File(args[1]), new File(args[2]));
		}
		else if(args[0].equals("dex2Smali"))
		{
			changeDex2Smali(new File(args[1]), new File(args[2]));
		}
		else if(args[0].equals("smali2Dex"))
		{
			changeSmali2Dex(new File(args[1]), new File(args[2]));
		}
		else if(args[0].equals("jar2Dex"))
		{
			jar2Dex(args[1], args[2]);
		}
		else if(args[0].equals("class2Dex"))
		{
			changeClass2Dex(args[1], args[2]);
		}
		else if(args[0].equals("signApk"))
		{
			signApk(args[1], args[2]);
		}
	}
	
	public static void changeDex2Jar(File dexFile, File outputFile)
	{
		Dex2jarCmd.main(new String[]{"--force","--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
	}
	
	public static void changeDex2Smali(File dexFile, File outputFile)
	{
		BaksmaliCmd.main(new String[]{"--force","--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
	}
	
	public static void changeSmali2Dex(File dexFile, File outputFile)
	{
		SmaliCmd.main(new String[]{"--output",outputFile.getAbsolutePath(),dexFile.getAbsolutePath()});
	}
	
	public static void jar2Dex(String jarPath, String outputPath) throws InterruptedException
	{
		Jar2Dex.main(new String[]{"--force","--output",outputPath,jarPath});
	}
	
	public static void changeClass2Dex(String classPath, String dexPath) throws InterruptedException
	{
		Jar2Dex.main(new String[]{"--force","--output",dexPath,classPath});
	}
	
	public static void signApk(String input, String output) throws InterruptedException
	{
		String[] argsList={"-f","-o",output,input};
		ApkSign.main(argsList);
	}

}
