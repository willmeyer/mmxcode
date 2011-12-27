package com.willmeyer.mmxcode;

import java.io.File;

public interface Encoder {

	/**
	 * Encodes the file.  Should throw an exception if newDestFile does not exists after returning (but this will be handled for it if its messy).
	 */
	public void encodeFromWav(File srcFileAbs, String options, String newDestFile) throws Exception;

	public String getSupportedTargetFormat();
	
}
