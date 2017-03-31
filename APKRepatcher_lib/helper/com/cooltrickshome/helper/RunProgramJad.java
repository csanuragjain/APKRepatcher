package com.cooltrickshome.helper;

import jadx.api.JadxDecompiler;

import jadx.core.utils.exceptions.JadxException;

import java.io.File;

public class RunProgramJad {

	/**
	 * @param args
	 * @throws JadxException 
	 */
	public static void main(String[] args) throws JadxException {
		// TODO Auto-generated method stub
		if(args[0].equals("jadx"))
		{
			decompileJar2Java(new File(args[1]), args[2]);
		}
	}
	
	public static void decompileJar2Java(File jarPath, String outputSourceDirectory) throws JadxException
	{
		 JadxDecompiler jadx = new JadxDecompiler();
		 jadx.setOutputDir(new File(outputSourceDirectory));
		 jadx.loadFile(jarPath);
		 jadx.save();
			if (jadx.getErrorsCount() != 0) {
				jadx.printErrorsReport();
			} else {
			}
	}

}
