package com.willmeyer.mmxcode;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;

public class RulesManager {
	
	/**
	 * The actual serializable rules definition
	 */
	protected static class RuleSet {

		public String name;
		public List<Rule> rules;
		public BlackList blackList;
		
	}

	protected RuleSet ruleSet;
	
	RulesManager(RuleSet ruleSet) throws Exception {
		this.ruleSet = ruleSet;
	}
	
	public String getRuleSetName() {
		return ruleSet.name;
	}
	
	public List<Rule> getRules() {
		return ruleSet.rules;
	}
	
	public BlackList getBlackList() {
		return ruleSet.blackList;
	}
	
	protected static RulesManager loadFromFile(File rulesFile) throws Exception {
		RuleSet set = null;
		Gson gson = new Gson();
		set = gson.fromJson(new InputStreamReader(new FileInputStream(rulesFile)), RuleSet.class);
		return new RulesManager(set);
	}
	
}
