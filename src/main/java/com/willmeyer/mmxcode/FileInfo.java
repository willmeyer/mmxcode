package com.willmeyer.mmxcode;

import java.io.File;

public class FileInfo {
	
	public FileInfo(File file) {
		fileAbs = file.getAbsolutePath();
		pathAbs = file.getParent();
		fileName = file.getName();
		fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
		fileNameNoExt = fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public String fileAbs;
	public String pathAbs;
	public String fileName;
	public String fileExt;
	public String fileNameNoExt;
}
