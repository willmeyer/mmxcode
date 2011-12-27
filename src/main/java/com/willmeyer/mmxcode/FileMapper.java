package com.willmeyer.mmxcode;

/**
 * A thing that can, given a source file, generate an appropriate dest file.  How it does this is up to 
 * different map implementations.
 * 
 * The mapper maps between two file extensions, and can, in addition to generating a dest from a source,
 * look at a dest file and figure out the source file that would have been used to generate it.
 * 
 * The mapper will be called upon whenever there is a file with the source extension encountered in the 
 * source tree.
 * 
 * For any source or dest file extension, there will be only one corresponding mapper.
 */
public abstract class FileMapper {

	protected String srcExt = null;
	protected String destExt = null;
	
	public FileMapper(String srcExt, String destExt) {
		assert(srcExt != null);
		assert(destExt != null);
		this.srcExt = srcExt;
		this.destExt = destExt;
	}
	
	/**
	 * Can this mapper handle this file as a source file, and map it to a dest file?
	 */
	public boolean canMapSourceFile(String absFile) {
		return srcExt.equals("*") || absFile.endsWith("." + srcExt);
	}
	
	/**
	 * Is this dest file one that was created by this mapper (that it could map back to a source file)?
	 */
	public boolean canTraceDestFile(String absFile) {
		return destExt.equals("*") || absFile.endsWith("." + destExt);
	}

	protected static String getFileExt(String filename) {
		String ext = "";
		int i = filename.lastIndexOf(".");
		if (i != -1)
			ext = filename.substring(i+1);
		return ext;
	}
	
	/**
	 * Get the name of the dest file that will be created once we map this source file to the mirror directory.
	 */
	public abstract String getMappedDestFileName(FileInfo srcFile, String destPath);
	
	/**
	 * Get the name of the source file that would have been mapped to this dest file.
	 */
	public abstract String traceSourceFromDest(FileInfo destFile, String sourcePath);

	/**
	 * Do the actual mapping of source to dest.
	 */
	public abstract void map(FileInfo file1, FileInfo file2, long destFileTime) throws ActionException;
	
	public abstract String getName();
}
