/**
 * 
 */
package com.willmeyer.mmxcode;

import java.io.File;

public class MkDirAction extends Action {
	
	private String _dirAbs;
	
	@Override
	public String getRelatedFile() {
		return _dirAbs;
	}

	@Override
	public String getShortName() {
		return "mkdir";
	}

	public MkDirAction(String basePathAbs, String dirName) {
		_dirAbs = basePathAbs + File.separatorChar + dirName;	
	}
	
	public MkDirAction(String dirAbs) {
		_dirAbs = dirAbs;
	}

	public String toString() {
		return "Create directory \"" + _dirAbs + "\"";
	}
	
	public void act() {
		Debug.println("Creating dir \"" + _dirAbs + "\"");
		new File(_dirAbs).mkdir();
	}
}