package com.willmeyer.mmxcode;

import java.io.*;

/**
 * A mapper that just maps one file to another by a straight copy.
 */
public final class CopyMapper extends FileMapper {

	public static final String NAME = "Copy";
	
	public CopyMapper(String ext) {
		super(ext, ext);
	}
	
	@Override
	public String getMappedDestFileName(FileInfo srcFile, String destPath) {
		return destPath + File.separator + srcFile.fileName;
	}

	@Override
	public String traceSourceFromDest(FileInfo destFile, String srcPath) {
		return srcPath + File.separator + destFile.fileName;
	}

	@Override
	public void map(FileInfo file1, FileInfo file2, long destFileTime) throws ActionException {
	    try {
	    	Debug.println("Copying \"" + file1.fileAbs + "\" to \"" + file2.fileAbs + "\"...");
			InputStream in = new FileInputStream(new File(file1.fileAbs));
	        OutputStream out = new FileOutputStream(new File(file2.fileAbs));
	    
	        // Transfer bytes from in to out
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
			new File(file2.fileAbs).setLastModified(destFileTime);
	    } catch (Exception e) {
	    	throw new ActionException("Unable to copy file.", file2.fileAbs, e);
	    }
    }

	public String getName() {
		return NAME;
	}
}
