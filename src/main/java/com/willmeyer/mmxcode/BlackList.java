/**
 * 
 */
package com.willmeyer.mmxcode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class BlackList {
	List<String> names;
	
	public BlackList() {
		names = new LinkedList<String>();
	}
	
	public void add(String pattern) {
		this.names.add(pattern);
		Collections.sort(names);
	}
	
	public boolean passes(String fileOrDir) {
		return !this.names.contains(fileOrDir);
	}
}