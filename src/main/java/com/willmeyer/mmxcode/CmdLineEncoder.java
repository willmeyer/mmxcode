package com.willmeyer.mmxcode;

import java.io.File;
import java.util.HashMap;

public class CmdLineEncoder extends CmdLineTool implements Encoder {

	private String format;
	
	public final String getSupportedTargetFormat() {
		return format;
	}
	
	public void encodeFromWav(File srcFileAbs, String options, String newDestFile) throws Exception {
		Debug.println("Encoding \"" + srcFileAbs.getAbsolutePath() + "\" to \"" + newDestFile + "\"...");
		HashMap<String, String> vars = new HashMap<String,String>();
		vars.put("INFILE", srcFileAbs.getAbsolutePath());
		vars.put("OUTFILE", newDestFile);
		this.executeCmdLine(vars);
	}

}
