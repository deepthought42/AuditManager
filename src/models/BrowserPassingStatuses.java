package com.looksee.models;

import java.util.Map;

/**
 * 
 * 
 */
public class BrowserPassingStatuses {
	private Map<String, Boolean> status_map;

	public Map<String, Boolean> getBrowserPassingStatuses() {
		return status_map;
	}

	public void setBrowserPassingStatuses(Map<String, Boolean> browser_passing_statuses) {
		this.status_map = browser_passing_statuses;
	}
}
