package com.willmeyer.mmxcode;

import java.io.*;

public class ActionLog {
	
	protected String fileName = "actionlog.csv";
	protected PrintWriter log;
	
	public ActionLog() throws Exception {
		File flog = new File(fileName);
		try {
			if (flog.exists()) flog.delete();
			log = new PrintWriter(new FileOutputStream(flog));
		} catch (Exception e) {
			throw new Exception ("Unable to open action log file \"" + flog.getAbsolutePath() + "\": " + e.getMessage());
		}
	}

	protected StringBuffer buildLine(String actionName, boolean success, String fileName, String extra) {
		StringBuffer line = new StringBuffer();
		line.append(success ? "OK" : "ERROR");
		line.append(',');
		line.append(actionName);
		line.append(',');
		line.append(fileName);
		line.append(',');
		if (extra == null) extra = "";
		line.append(extra);
		return line;
	}
	
	public void addSuccessfulAction(Action action) {
		log.println(this.buildLine(action.getShortName(), true, action.getRelatedFile(), null));
		log.flush();
	}
	
	public void addErrorAction(Action action, ActionException e) {
		log.println(this.buildLine(action.getShortName(), false, action.getRelatedFile(), e.getMessage()));
		log.flush();
	}
}
