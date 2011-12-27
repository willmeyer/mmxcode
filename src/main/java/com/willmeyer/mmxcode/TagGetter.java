package com.willmeyer.mmxcode;

import java.io.File;

public interface TagGetter {
	
	public String getSupportedFormat();

	public TagSet getTags(File fileAbs) throws Exception;
	
}
