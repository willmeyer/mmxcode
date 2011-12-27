package com.willmeyer.mmxcode;

public class ActionException extends Exception {

	private Exception _underlying;
	private String _relatedFile;
	
	public ActionException(String msg, String relatedFile, Exception underlying) {
		super(msg + " (" + underlying.getMessage() + ")");
		_underlying = underlying;
		_relatedFile = relatedFile;
	}
	
	public ActionException(String msg, String relatedFile) {
		super(msg);
		_underlying = null;
		_relatedFile = relatedFile;
	}

	public Exception getUnderlying() {
		return _underlying;
	}
	
	public String relatedFile() {
		return this._relatedFile;
	}
	
	public String getTextSummary() {
		String detail = this.getMessage();
		detail += "\nRelated file: \"" + this._relatedFile + "\"";
		return detail;
	}
}