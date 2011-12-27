package com.willmeyer.mmxcode;

import java.io.File;

public interface Decoder {

	public String getSupportedSourceFormat();
	
	/**
	 * Decodes the file from the understood source format to WAV.  Should throw an exception if newDestFile does not exists after 
	 * returning (but this will be handled for it if its messy).
	 */
	public void decodeToWav(File srcFileAbs, String options, String newDestFile) throws Exception;
	
}
