package com.willmeyer.mmxcode;

import java.util.*;
import java.io.*;

public abstract class CmdLineTool {
		
	private String command;
	private String platform;
	private int expectedExitCode = 0;
	private boolean checkExitCode = true;

	protected static class StreamConsumer extends Thread
	{
	    InputStream is;
	    StringBuffer content = new StringBuffer();
	    
	    public StreamConsumer(InputStream is) {
	        this.is = is;
	    }
	    
	    public void run() {
	    	try {
				int ch;
	    		while(-1 != (ch= is.read())) { 
					content.append((char)ch);
				}
	        } catch (IOException ioe) {
	        	ioe.printStackTrace();  
	        }
	    }
	    
	    public StringBuffer getContent() {
	    	return content;
	    }
	}
	
	public static class CmdLineResult {
		StringBuffer stderr;
		StringBuffer stdout;
		int exitCode;
		
		public CmdLineResult(int exitCode, StringBuffer stdout, StringBuffer stderr) {
			this.exitCode = exitCode;
			this.stdout= stdout;
			this.stderr = stderr;
		}
	}

	/**
	 * Is this tool available on the current platform?  Checks platform string and compares.
	 */
	public boolean availOnPlatform() {
		String os = System.getProperty("os.name");
		Debug.println("Tool supports platform: " + platform);
		if (platform.equalsIgnoreCase("win")) {
			return os.contains("Windows");
		} else if (platform.equalsIgnoreCase("osx")) {
			return os.contains("Mac OS X");
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the command-line to use, for a given platform.
	 * 
	 * @param cmdLine
	 * @param platform
	 */
	public void setCmdLine(String cmdLine, String platform) {
		this.command = cmdLine;
		this.platform = platform;
	}
	
	/**
	 * Executes the command-line, templating in all of the passed variables.
	 * 
	 * @param templateVars
	 */
	public CmdLineResult executeCmdLine(HashMap<String,String> templateVars) throws Exception {
		String args[] = buildParams(templateVars);
		String toExec = "";
		CmdLineResult ret = null;
		for (String arg : args) {
			toExec += (arg + " ");
		}
		Debug.println("CMD: " + toExec);
		if (this.platform.equalsIgnoreCase("osx") || this.platform.equalsIgnoreCase("win")) {
			try {
				
				// Start up the processes and start reading from the streams
				Process proc = Runtime.getRuntime().exec(args);
				InputStream stdout = proc.getInputStream();
				InputStream stderr = proc.getErrorStream();
				StreamConsumer stderrReader = new StreamConsumer(stderr);
				stderrReader.start();
				StreamConsumer stdoutReader = new StreamConsumer(stdout);
				stdoutReader.start();
				
				// Wait for the process to finish
				int exitCode = proc.waitFor();
				Thread.sleep(500);
				Debug.println("Finished executing.");
				
				// Get results
				StringBuffer errStr = stderrReader.getContent();
				StringBuffer outStr = stdoutReader.getContent();

				// Check exit code
				if (this.checkExitCode && (exitCode != expectedExitCode)) {
					if (Debug.ON) {
						Debug.println("Unexpected exit code: " + exitCode);
						this.dumpProcessOutput(outStr, errStr);
					}
					throw new Exception ("Process exited with unexpected code (" + exitCode +", expected " + expectedExitCode + ")");
				}
				if (Debug.EXEC_OUTPUT) {
					this.dumpProcessOutput(outStr, errStr);
				}
				if (exitCode != 0) throw new Exception ("Process exited with non-zero code (" + exitCode +"), stderr: " + errStr.toString());
				ret = new CmdLineResult(exitCode, outStr, errStr);
				return ret;
			} catch (FileNotFoundException e) {
				throw new Exception ("Executable file could not be found (\"" + toExec + "\")...is it valid on this system?");
			} catch (IOException e) {
				throw new Exception ("Failure executing command (\"" + toExec + "\")...is it valid on this system?");
			} catch (Exception e) {
				throw new Exception ("Failure while executing command (\"" + toExec + "\"): " + e.getMessage());
			}
		}
		return ret;
	}
	
	protected void dumpProcessOutput(StringBuffer stdoutStr, StringBuffer stderrStr) {
		Debug.println("----------------------------");
		Debug.println("STDERR: (" + stderrStr.length() + " chars)");
		Debug.println();
		Debug.println(stderrStr.toString());
		Debug.println("STDOUT: (" + stdoutStr.length() + " chars)");
		Debug.println();
		Debug.println(stdoutStr.toString());
		Debug.println("----------------------------");
	}
	
	public String[] buildParams(HashMap<String,String> templateVars) {
		assert (command != null);
		String[] args = command.split(" ");
		for (int i = 0; i < args.length; i++) {
			for (String key : templateVars.keySet()) {
				args[i] = args[i].replace("{" + key + "}", templateVars.get(key));
			}
		}
		return args;
	}
	

}
