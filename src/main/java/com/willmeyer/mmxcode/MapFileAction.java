package com.willmeyer.mmxcode;

import java.io.File;

public class MapFileAction extends Action {
	
	private FileMapper mapper;
	private File srcFile;
	private File destFile;
	private long destFileTime;
	
	public MapFileAction(FileMapper mapper, File srcFile, File destFile, long destFileTime) {
		this.mapper = mapper;
		this.destFile = destFile;
		this.srcFile = srcFile;
		this.destFileTime = destFileTime;
	}
	
	@Override
	public String getRelatedFile() {
		return srcFile.getAbsolutePath();
	}

	@Override
	public String getShortName() {
		return "map: " + mapper.getName();
	}

	public String toString() {
		return mapper.getName() + " file \"" + srcFile + "\" --> \"" + destFile + "\"";
	}

	@Override
	public void act() throws ActionException {
		mapper.map(new FileInfo(srcFile), new FileInfo(destFile), destFileTime);
	}
	
}