package com.willmeyer.mmxcode;

import java.util.*;

public class Format {

	protected String name;
	protected List<String> extensions;
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getExtensions() {
		return this.extensions;
	}
	
	public boolean checkByExtention(String extension) {
		return this.extensions.contains(extension);
	}
}
