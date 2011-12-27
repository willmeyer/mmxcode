package com.willmeyer.mmxcode;

import java.util.*;
import java.io.*;

import com.google.gson.*;

/**
 * A set of Format objects.  
 */
public class Formats {

	protected boolean init = false;
	protected List<Format> formats;
	protected HashMap<String, Format> extensionToFormat = null;
	protected HashMap<String, Format> nameToFormat = null;
	
	protected void initMapAsNeeded() {
		if (!init) {
			this.extensionToFormat = new HashMap<String, Format>();
			this.nameToFormat = new HashMap<String, Format>();
			for (Format format : this.formats) {
				for (String extension : format.extensions) {
					this.extensionToFormat.put(extension.toLowerCase(), format);
				}
				this.nameToFormat.put(format.getName().toLowerCase(), format);
			}
			init = true;
		}
	}
	
	public Format getByExtension(String extension) {
		extension = extension.toLowerCase();
		this.initMapAsNeeded();
		return this.extensionToFormat.get(extension);
	}
	
	public Format getByName(String name) {
		name = name.toLowerCase();
		this.initMapAsNeeded();
		return this.nameToFormat.get(name);
	}

	public static Formats loadFromFile(File formatsFile) throws Exception {
		Gson gson = new Gson();
		Formats formats = gson.fromJson(new InputStreamReader(new FileInputStream(formatsFile)), Formats.class);
		return formats;
	}
}
