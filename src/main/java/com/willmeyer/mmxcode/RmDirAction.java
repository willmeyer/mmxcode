package com.willmeyer.mmxcode;

import java.io.File;

public class RmDirAction extends Action {
	
	private String _dirAbs;
	
	public RmDirAction(String dirAbs) {
		_dirAbs = dirAbs;
	}
	
	public String toString() {
		return "Delete directory \"" + _dirAbs + "\"";
	}

	@Override
	public String getRelatedFile() {
		return _dirAbs;
	}

	@Override
	public String getShortName() {
		return "rmdir";
	}

	public void act() {
		Debug.println("Deleting dir \"" + _dirAbs + "\"");
		new File(_dirAbs).delete();
	}

}