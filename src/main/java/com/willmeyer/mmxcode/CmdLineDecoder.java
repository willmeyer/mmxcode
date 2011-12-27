package com.willmeyer.mmxcode;

import java.io.File;
import java.util.HashMap;

public class CmdLineDecoder extends CmdLineTool implements Decoder {

	public String format;

	public CmdLineDecoder() {
	}

	public final String getSupportedSourceFormat() {
		return format;
	}

	public void decodeToWav(File srcFileAbs, String options, String newDestFile) throws Exception {
		Debug.println("Decoding \"" + srcFileAbs.getAbsolutePath() + "\" to \"" + newDestFile + "\"...");
		HashMap<String, String> vars = new HashMap<String,String>();
		vars.put("INFILE", srcFileAbs.getAbsolutePath());
		vars.put("OUTFILE", newDestFile);
		this.executeCmdLine(vars);
	}

}
