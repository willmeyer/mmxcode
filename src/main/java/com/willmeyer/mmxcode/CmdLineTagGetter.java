package com.willmeyer.mmxcode;

import java.io.File;
import java.util.HashMap;
import java.util.regex.*;

public class CmdLineTagGetter extends CmdLineTool implements TagGetter {

	private String format;
	private TagSet matcher;

	public final String getSupportedFormat() {
		return format;
	}

	private String matchStr(String haystack, String match) {
		Pattern pattern = Pattern.compile(match);
		Matcher matcher = pattern.matcher(haystack); 
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	public TagSet getTags(File fileAbs) throws Exception {
		Debug.println("Getting tags from \"" + fileAbs.getAbsolutePath() + "\"...");
		HashMap<String, String> vars = new HashMap<String,String>();
		vars.put("INFILE", fileAbs.getAbsolutePath());
		CmdLineResult result = this.executeCmdLine(vars);
		String parseable = null;
		if (result.stdout.length() > 5) {
			parseable = result.stdout.toString();
		} else {
			parseable = result.stderr.toString();	
		}
		if (Debug.EXEC_OUTPUT) {
			Debug.println("Parsing tags from (" + parseable.length() + " chars): ");
			Debug.println();
			Debug.println(parseable);
			Debug.println();
		}
		TagSet tags = new TagSet();
		tags.trackTitle = matchStr(parseable, matcher.trackTitle);
		tags.trackNum = matchStr(parseable, matcher.trackNum);
		tags.album = matchStr(parseable, matcher.album);
		tags.artist = matchStr(parseable, matcher.artist);
		tags.genre = matchStr(parseable, matcher.genre);
		tags.year = matchStr(parseable, matcher.year);
		return tags;
	}
	
}
