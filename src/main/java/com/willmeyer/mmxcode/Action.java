package com.willmeyer.mmxcode;


/**
 * An Action is something that can be executed by the tool.  Actions operate on files or directories. 
 * That's it.
 */
public abstract class Action {

	public abstract void act() throws ActionException;

	public abstract String getShortName();
	
	/**
	 * Gets the file or directory that the action is relevant for (if there are multiple, this 
	 * should be the logically most significant one, like a source file)
	 */
	public abstract String getRelatedFile();
}