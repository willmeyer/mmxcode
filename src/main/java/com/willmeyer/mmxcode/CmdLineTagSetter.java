package com.willmeyer.mmxcode;

import java.io.File;
import java.util.HashMap;

public class CmdLineTagSetter extends CmdLineTool implements TagSetter {

	private String format;
	
	public CmdLineTagSetter() {
	}
	
	public final String getSupportedFormat() {
		return format;
	}

	public void setTags(File fileAbs, TagSet tags) throws Exception {
		System.out.println("Setting tags for \"" + fileAbs.getAbsolutePath() + "...");
		HashMap<String, String> vars = new HashMap<String,String>();
		vars.put("INFILE", fileAbs.getAbsolutePath());
		vars.put("ARTIST", tags.artist);
		vars.put("ALBUM", tags.album);
		vars.put("GENRE", tags.genre);
		vars.put("YEAR", tags.year);
		vars.put("TRACKTITLE", tags.trackTitle);
		vars.put("TRACKNUM", tags.trackNum);
		this.executeCmdLine(vars);
	}

}
