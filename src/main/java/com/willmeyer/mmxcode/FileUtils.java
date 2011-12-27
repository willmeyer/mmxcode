package com.willmeyer.mmxcode;

import java.io.File;

public abstract class FileUtils {

	/**
	 * Trims the long file path of a file down to just the part relative to the root provided.
	 * 
	 * @param rootFilePath The root part of the path (abs)
	 * @param fullFilePath The complete abs path to the file
	 * @param includeRootEnd If true, include the last part of the root in the resulting path, 
	 * 						 otherwise trim don't show the root at all
	 * @return The resulting path
	 */
	public static String trimPath(String rootFilePath, String fullFilePath, boolean includeRootEnd) {
		
		String beginPath = "";
		
		// Get just the last part of the root dir, if desired
		if (includeRootEnd) {
			int i = rootFilePath.lastIndexOf(File.separator);
			if (rootFilePath.length() - i < 2) {
				i = rootFilePath.lastIndexOf(File.separator, i);
			}
			beginPath = rootFilePath.substring(i);
		}
		
		// Get just the relative part
		String relPath = fullFilePath.substring(rootFilePath.length());
		
		// Assemble and done
		return beginPath + relPath;
	}

}
