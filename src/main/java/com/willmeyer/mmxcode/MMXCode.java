package com.willmeyer.mmxcode;

import java.io.*;
import java.util.*;

import joptsimple.*;

public abstract class MMXCode {

	protected MMXCode() {
	}

	private static void printUsage (OptionParser parser) {
		try {
			System.out.println("Usage:");
			System.out.println();
			System.out.println("    mmxcode [options] masterDir mirrorDir");
			System.out.println();
			PrintWriter writer = new PrintWriter(System.out);
			parser.printHelpOn(writer);
			writer.flush();
		} catch (Exception e2) {}
	}
	
	public static void main(String[] args) {
		System.out.println("*** MediaMirror Transcoder (MMXCode) v0.2 ***");
		System.out.println("");

		// Set up and parse command-line input, building up options for the run
		OptionParser parser = new OptionParser();
		OptionSet options = null;
	    String srcRootAbs;
	    String destRootAbs;
	    boolean dryRun = false;
	    boolean fixupDest = false;
	    boolean debug = false;
	    boolean testFormats = false;
	    File rulesFile = new File("conf/rules.json");
	    File toolsFile = new File("conf/tools.json");
	    File formatsFile = new File("conf/formats.json");
	    String singleDir = null;
		try {
			parser.accepts("toolsfile", "The path to the tools definition file (conf/tools.json by default").withRequiredArg().ofType(String.class);
			parser.accepts("rulesfile", "The path to the rules definition file (conf/rules.json by default").withRequiredArg().ofType(String.class);
			parser.accepts("formatsfile", "The path to the formats definition file (conf/formats.json by default").withRequiredArg().ofType(String.class);
		    parser.accepts("dryrun", "Does a dry-run only, no actual modifications"); 
		    parser.accepts("clean", "Clean mode enabled; cleans out old files/dirs from the mirror as needed");
		    parser.accepts("single", "Run for just this single directory, within the master tree").withRequiredArg().ofType(String.class);;
		    parser.accepts("help", "Get help");
		    parser.accepts("fmttest", "Test format encoder/deoder support (instead of actually doing any mirroring)");
		    parser.accepts("debug", "Debug mode; dumps more detail");
		    options = parser.parse(args);			

			// Get options, and handle help
			if (options.has("help")) {
				System.out.println("This tool helps you manage a transcoded mirror of audio files, syncing a master and a mirror directory tree, transcoding and copying along the way.  See the usage docs for more info.");
				System.out.println("");
				printUsage(parser);
				return;
			}
		    dryRun = options.has("dryrun");
			fixupDest = options.has("clean");
			debug = options.has("debug");
			Debug.ON = debug;
			if (options.has("rulesfile")) {
				rulesFile = new File((String)options.valueOf("rulesfile"));
			}
			if (options.has("toolsfile")) {
				toolsFile = new File((String)options.valueOf("toolsfile"));
			}
			if (options.has("formatsfile")) {
				toolsFile = new File((String)options.valueOf("formatsfile"));
			}
			if (options.has("single")) {
				singleDir = (String)options.valueOf("single");
				singleDir = new File(singleDir).getAbsolutePath();
			}
			if (options.has("fmttest")) {
				testFormats = true;
			}
		} catch (Exception e) {
			if (e instanceof OptionException) {			
				System.out.println("ERROR: Bad usage: " + ((OptionException)e).getMessage());
			} else {
				System.out.println("ERROR: Bad usage: " + e.getMessage());
			}
			printUsage(parser);
			return;
		}

		// Ok now do some real work
		try {

			// Load formats
			System.out.println("Using formats defined in \"" + formatsFile.getAbsolutePath() + "\"...");
			verifyConfigFile("Formats file", formatsFile);
			Formats formats = Formats.loadFromFile(formatsFile);
			System.out.println("Loaded formats.");
			
			// Load installed tools
			System.out.println("Using tools defined in \"" + toolsFile.getAbsolutePath() + "\"...");
			verifyConfigFile("tools file", toolsFile);
			ToolsManager toolsMgr = ToolsManager.loadFromFile(toolsFile);
			System.out.println("Loaded tools.");
			
			// Testing formats, or doing a real pass?
			if (testFormats) {
				
				// Testing formats only, we have what we need
				doFormatTests(formats, toolsMgr);
			} else {
				
				// Get and verify master and mirror dirs
			    List<String> nonOpts = options.nonOptionArguments();
			    if (nonOpts.size() != 2) {
			    	throw new Exception("Missing master and/or mirror paths");
			    }
			    srcRootAbs = new File(nonOpts.get(0)).getAbsolutePath();
			    destRootAbs = new File(nonOpts.get(1)).getAbsolutePath();
				if (!new File(srcRootAbs).exists() || !new File(destRootAbs).exists()) {
					throw new Exception ("Master and mirror paths MUST exist (" + srcRootAbs + " and " + destRootAbs + ")");
				}
				
				// Do the sync
				doSync(formats, srcRootAbs, destRootAbs, rulesFile, toolsMgr, fixupDest, dryRun, singleDir);
			}
			
			// Done
			System.out.println("");
			System.out.println("Done.");
			System.exit(0);
		} catch (Exception e) {
			if (debug) e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("Use '--help' if you need help");
		}
		System.exit(1);
	}

	private static void verifyConfigFile(String logicalName, File file) throws Exception {
		if (!file.exists() || !file.canRead()) {
			throw new Exception("Unable to find/read " + logicalName + " at \"" + file.getAbsolutePath() + "\"");
		}
	}
	
	private static void doFormatTests(Formats formats, ToolsManager toolsMgr) throws Exception {
		List<String> confirmedEncoders = new LinkedList<String>();
		List<String> confirmedDecoders = new LinkedList<String>();
		
		// Verify all installed decoders work by decoding well-known test files
		System.out.println("Testing installed decoders...");
		for (Decoder decoder : toolsMgr.getDecoders()) {
			String format = decoder.getSupportedSourceFormat();
			String ext = formats.getByName(format).getExtensions().get(0);
			System.out.println("Testing " + format + " decoder...");
			String testFile = ("test-media/formats/test." + ext).toLowerCase();
			File inFile = new File(testFile);
			if (inFile.exists() && inFile.canRead()) {
				String outFileAbs = inFile.getAbsolutePath() + ".wav";
				try {
					decoder.decodeToWav(inFile, null, outFileAbs);
					File outFile = new File(outFileAbs); 
					if (outFile.exists()) {
						outFile.delete();
					} else {
						throw new Exception ("Decoder ran, but no output file was created.");
					}
					System.out.println("    OK.");
					confirmedDecoders.add(format + "(." + ext + ")");
				} catch (Exception e) {
					System.out.println("ERROR: " + format + " decoder failed to decode the file, it may not be set up correctly on your system (check your tool defs file). " + e.getMessage());
				}
			} else {
				System.out.println("ERROR: No reference file for this format; unable to test this decoder (file \"" + inFile.getAbsolutePath() + "\" not found)");
			}
		}

		// Verify all installed encoders work by encoding a well-known wav file
		System.out.println("Testing installed encoders...");
		File testWavFile = new File("test-media/formats/test.wav");
		if (testWavFile.exists() && testWavFile.canRead()) {
			for (Encoder encoder : toolsMgr.getEncoders()) {
				String format = encoder.getSupportedTargetFormat();
				String ext = formats.getByName(format).getExtensions().get(0);
				System.out.println("Testing " + format + " encoder...");
				File outFile = new File(testWavFile.getAbsolutePath() + "." + ext);
				try {
					encoder.encodeFromWav(testWavFile, null, outFile.getAbsolutePath());
					if (outFile.exists()) {
						outFile.delete();
					} else {
						throw new Exception ("Encoder ran, but no output file was created.");
					}
					System.out.println("    OK.");
					confirmedEncoders.add(format + "(." + ext + ")");
				} catch (Exception e) {
					System.out.println("ERROR: " + format + " encoder failed to encode the file, it may not be set up correctly on your system (check your tool defs file). " + e.getMessage());
				}
			} 
		} else {
			System.out.println("ERROR: No reference wav file was found; unable to test any encoders (file \"" + testWavFile.getAbsolutePath() + "\" not found)");
		}
		
		// Summary
		System.out.println();
		System.out.println("SUMMARY:");
		System.out.println();
		if (confirmedEncoders.size() > 0) {
			System.out.println("  Available encoders:");
			for (String name : confirmedEncoders) System.out.println("    " + name);
		} else {
			System.out.println("No encoders available!");
		}
		System.out.println();
		if (confirmedDecoders.size() > 0) {
			System.out.println("  Available decoders:");
			for (String name : confirmedDecoders) System.out.println("    " + name);
		} else {
			System.out.println("No decoders available!");
		}

	}
	
	private static void doSync(Formats formats, String srcRootAbs, String destRootAbs, File rulesFile, ToolsManager toolsMgr, boolean fixupDest, boolean dryRun, String singleDir) throws Exception {

		// Load configured rules
		System.out.println("Using rules defined in \"" + rulesFile.getAbsolutePath() + "\"...");
		verifyConfigFile("rules file", rulesFile);
		RulesManager rulesMgr = RulesManager.loadFromFile(rulesFile);
		List<Rule> rules = rulesMgr.getRules();
		System.out.println("Loaded rules (\"" + rulesMgr.getRuleSetName() + "\").");
		
		// Set up file mappers (including transcoders) based on the configured rules
		Debug.println("Setting up mappers...");
		ArrayList<FileMapper> mappers = new ArrayList<FileMapper>();
		for (Rule rule : rules) {

			// Make sure the formats are known
			Format srcFormat = formats.getByName(rule.source);
			if (srcFormat == null) {
				throw new Exception("The format \"" + rule.source + "\" is not a known format, no rule can be set up to use it.");
			}
			String srcExt = srcFormat.getExtensions().get(0);
			Format destFormat = formats.getByName(rule.dest);
			if (destFormat == null) {
				throw new Exception("The format \"" + rule.dest + "\" is not a known format, no rule can be set up to use it.");
			}
			String destExt = destFormat.getExtensions().get(0);
			
			// Now set up the specific type of mapper (we now know the formats are good)
			if (rule.mapper.equalsIgnoreCase(TranscodingMapper.NAME)) {
				
				// Transcoding rule, set up decoder, encoder, and tag getter and setter 
				Decoder decoder = toolsMgr.getDecoder(rule.source);
				if (decoder == null)
					throw new Exception ("No suitable decoder is available for file type \"" + rule.source + "\"");
				Encoder encoder = toolsMgr.getEncoder(rule.dest);
				if (encoder == null)
					throw new Exception ("No suitable encoder is available for file type \"" + rule.dest + "\"");
				TagGetter getter = toolsMgr.getTagGetter(rule.source);
				if (getter == null)
					throw new Exception ("No suitable tag getter is available for file type \"" + rule.source + "\"");
				TagSetter setter = toolsMgr.getTagSetter(rule.dest);
				if (setter == null)
					throw new Exception ("No suitable tag setter is available for file type \"" + rule.dest + "\"");
				TranscodingMapper trans = new TranscodingMapper(srcExt, destExt, decoder, encoder, getter, setter);
				mappers.add(trans);
			} else if (rule.mapper.equalsIgnoreCase(CopyMapper.NAME)) {
				
				// Simple copy mapper...
				mappers.add(new CopyMapper(srcExt));
			}
		}
		Debug.println("Processors created.");
		
		// Figure out the set of required actions
		System.out.println("Building action list for mapped dirs (\"" + srcRootAbs + "\" --> \"" + destRootAbs + "\")");
		ArrayList<Action> actions = buildActionList(srcRootAbs, destRootAbs, mappers, fixupDest, rulesMgr.getBlackList(), singleDir);
		
		// Process all of the required actions
		runActions(actions, dryRun);
		
	}
	
	/**
	 * Execute the set of Actions defined for this run.
	 */
	private static void runActions(ArrayList<Action> actions, boolean dryRun) throws Exception {
		ActionLog log = null;
		if (!dryRun) log = new ActionLog();
		System.out.println("");
		System.out.println("SUMMARY:");
		System.out.println("   " + actions.size() + " actions needed.");
		System.out.println("");
		int num = actions.size();
		int errCount = 0;
		int i = 1;
		if (num > 0) {
			for (Action action : actions) {
				System.out.println("" + i + "/" + num + ": " + action.toString());
				if (!dryRun) {
					try {
						action.act();
						log.addSuccessfulAction(action);
					} catch (ActionException e) {
						errCount++;
						System.out.println("ERROR----------------");
						System.out.println(e.getTextSummary());
						System.out.println("---------------------");
						log.addErrorAction(action, e);
					}
				}
				i++;
			}
		}
		System.out.println();
		System.out.println("COMPLETION SUMMARY:");
		if (!dryRun) {
			System.out.println("   " + actions.size() + " actions identified and executed");
			System.out.println("   " + errCount + " actions resulted in errors");
		} else {
			System.out.println("   " + actions.size() + " actions identified, but not executed (dry-run mode)");
		}
	}
	
	/**
	 * Builds the set of actions necessary to sync the directories in their entirety.
	 */
	public static ArrayList<Action> buildActionList(String srcRootAbs, String destRootAbs, ArrayList<FileMapper> processors, boolean fixupDest, BlackList blackList, String singleDirAbs) throws Exception {
		ArrayList<Action> actions = new ArrayList<Action>(); 
		
		// Are we starting with a specific relative-to-root directory?
		String rootRelative = "";
		if (singleDirAbs != null) {
			if (!singleDirAbs.startsWith(srcRootAbs)) throw new Exception("Single directory \"" + singleDirAbs + "\" is not within the specified master directory.");
			rootRelative = singleDirAbs.substring(srcRootAbs.length());
		}
		
		// First traverse the source tree, building actions to add things in source to destination
		processDir(true, actions, srcRootAbs, destRootAbs, rootRelative, processors, blackList);
		
		// Now traverse the destination tree, removing things from source that are not in the destination
		if (fixupDest) {
			processDir(false, actions, destRootAbs, srcRootAbs, rootRelative, processors, blackList);
		}
		return actions;
	}

	private static String trimToRoot(String rootFilePath, String longFilePath) {
		return longFilePath.substring(rootFilePath.length());
	}

	/**
	 * Process two directories, all children and all files, generating the set of actions necessary to sync them.  
	 */
	public static void processDir(boolean isSrc, ArrayList<Action> actions, String root1Abs, String root2Abs, String dirPathRel, ArrayList<FileMapper> processors, BlackList blackList) {
		File dir1 = new File(root1Abs + File.separator + dirPathRel);
		assert (dir1.isDirectory());
		File dir2 = new File(root2Abs + File.separator + dirPathRel);
		String dir1Disp = FileUtils.trimPath(root1Abs, dir1.getAbsolutePath(), false);
		String dir2Disp = FileUtils.trimPath(root2Abs, dir2.getAbsolutePath(), false);
		System.out.println("Comparing \"" + dir1Disp + "\" --> \"" + dir2Disp + "\"");
		
		// Handle our peer directory
		if (isSrc && !dir2.exists()) {
			Debug.println(    "\"" + dir2 + "\" does not exist, it needs to be added.");
			actions.add(new MkDirAction(dir2.getAbsolutePath()));
		} else if (!isSrc && !dir2.exists()) {
			Debug.println(dir2 + "does not exist, so " + dir1 + " needs to be deleted.");
			actions.add(new RmDirAction(dir1.getAbsolutePath()));
		}
		
		// Traverse into the dir
		for (File file : dir1.listFiles()) {
			System.out.println("Analyzing \"" + trimToRoot(root1Abs, file.getAbsolutePath()) + "\"");
			if (!file.canRead()) {
				Debug.println("    unreadable...skipping.");
			} else if (!blackList.passes(file.getName())) {
				Debug.println("    blacklisted...skipping.");
			} else if (file.isDirectory()) {
				Debug.println("    is a directory...recursing...");
				processDir(isSrc, actions, root1Abs, root2Abs, dirPathRel + File.separator + file.getName(), processors, blackList);
			} else {
				Debug.println("    is a file...looking for processors...");
				if (isSrc) {
					for (FileMapper handler : processors) {
						if (handler.canMapSourceFile(file.getAbsolutePath())) {
							Debug.println("    found potential handler for file: " + handler.getName());
							File destFile = new File(handler.getMappedDestFileName(new FileInfo(file), dir2.getAbsolutePath()));
							if (destFile.exists() && destFile.lastModified() == file.lastModified()) {
								Debug.println("    files identical, no update needed.");
							} else {
								Debug.println("    adding handler action for file \"" + file.getAbsolutePath() + "\".");
								actions.add(new MapFileAction(handler, file, destFile, file.lastModified()));
							}
						}	
					}
				} else {
					boolean foundSrc = false;
					for (FileMapper handler : processors) {
						if (handler.canTraceDestFile(file.getAbsolutePath())) {
							File srcFile = new File(handler.traceSourceFromDest(new FileInfo(file), dir2.getAbsolutePath()));
							if (srcFile.exists()) {
								foundSrc = true;
							}
						}	
					}
					if (!foundSrc) {
						actions.add(new RmFileAction(file.getAbsolutePath()));
					}
				}
			}
		}
		
	}

}
