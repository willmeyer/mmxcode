package com.willmeyer.mmxcode;

import java.util.*;
import java.io.*;

import com.google.gson.*;

/**
 * Keeps track of "tools", command-line utilities which can operate on files of different formats. Encoders, 
 * Decoders, tag Getters and Setters.
 * 
 * All tools are managed relative to formats they know how to deal with.
 * 
 * ToolDefs is the JSON-friendly format.
 */
public class ToolsManager {

	private HashMap<String, Decoder> fmtToDecoder = new HashMap<String, Decoder>();
	private HashMap<String, Encoder> fmtToEncoder = new HashMap<String, Encoder>();
	private HashMap<String, TagSetter> fmtToSetter = new HashMap<String, TagSetter>();
	private HashMap<String, TagGetter> fmtToGetter = new HashMap<String, TagGetter>();
	
	public static class ToolDefs {
	
		private List<CmdLineDecoder> decoders;
		private List<CmdLineEncoder> encoders;
		private List<CmdLineTagSetter> tagSetters;
		private List<CmdLineTagGetter> tagGetters;
	}
	
	ToolsManager(ToolDefs defs) {
		
		// Add the loaded tools to our internal format-based maps
		Debug.println("Loaded " + defs.encoders.size() + " encoder definitions.");
		for (Encoder encoder : defs.encoders) {
			if (((CmdLineEncoder)encoder).availOnPlatform()) {
				Debug.println("Adding encoder for \"" + encoder.getSupportedTargetFormat() + "\".");
				fmtToEncoder.put(encoder.getSupportedTargetFormat().toLowerCase(), encoder);
			}
		}
		Debug.println("Loaded " + defs.decoders.size() + " decoder definitions.");
		for (Decoder decoder : defs.decoders) {
			if (((CmdLineDecoder)decoder).availOnPlatform()) {
				Debug.println("Adding decoder for \"" + decoder.getSupportedSourceFormat() + "\".");
				fmtToDecoder.put(decoder.getSupportedSourceFormat().toLowerCase(), decoder);
			}
		}
		Debug.println("Loaded " + defs.tagSetters.size() + " tag setter definitions.");
		for (TagSetter setter : defs.tagSetters) {
			if (((CmdLineTagSetter)setter).availOnPlatform()) {
				Debug.println("Adding tag setter for \"" + setter.getSupportedFormat() + "\".");
				fmtToSetter.put(setter.getSupportedFormat().toLowerCase(), setter);
			}
		}
		Debug.println("Loaded " + defs.tagGetters.size() + " tag getter definitions.");
		for (TagGetter getter : defs.tagGetters) {
			if (((CmdLineTagGetter)getter).availOnPlatform()) {
				Debug.println("Adding getter for \"" + getter.getSupportedFormat() + "\".");
				fmtToGetter.put(getter.getSupportedFormat().toLowerCase(), getter);
			}
		}
		
		// Any other tools...
		fmtToSetter.put("mp3", new MP3TagSetter());
	}
	
	public static ToolsManager loadFromFile(File toolsFile) throws Exception {
		Debug.println("Loading tool definitions from \"" + toolsFile.getAbsolutePath() + "\"...");
		ToolDefs defs = null;
		Gson gson = new Gson();
		defs = gson.fromJson(new InputStreamReader(new FileInputStream(toolsFile)), ToolDefs.class);
		return new ToolsManager(defs);
	}
	
	public Encoder getEncoder(String format) {
		return fmtToEncoder.get(format);
	}
	
	public Collection<Decoder> getDecoders() {
		return fmtToDecoder.values();
	}
	
	public Collection<Encoder> getEncoders() {
		return fmtToEncoder.values();
	}
	
	public Decoder getDecoder(String format) {
		return fmtToDecoder.get(format);
	}
	
	public TagGetter getTagGetter(String format) {
		return fmtToGetter.get(format);
	}

	public TagSetter getTagSetter(String format) {
		return fmtToSetter.get(format);
	}

}
