package com.willmeyer.mmxcode;

import java.io.File;

public interface TagSetter {

	public String getSupportedFormat();

	public void setTags(File fileAbs, TagSet tags) throws Exception;
}
