package com.willmeyer.mmxcode;

import java.io.File;

public class RmFileAction extends Action {
	
	private String _fileAbs;
	
	public RmFileAction(String fileAbs) {
		_fileAbs = fileAbs;
	}
	
	public String toString() {
		return "Delete file \"" + _fileAbs + "\"";
	}

	@Override
	public String getRelatedFile() {
		return _fileAbs;
	}

	@Override
	public String getShortName() {
		return "rm";
	}

	public void act() {
		Debug.println("Deleting file \"" + _fileAbs + "\"");
		new File(_fileAbs).delete();
	}

}