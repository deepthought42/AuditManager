package com.looksee.models.audit.performance;


/**
 * Defines detail item for "bootup-time" in the Google PageSpeed API 
 */
public class BootUpTime extends AuditDetail {
	
	private String url;
	private Double total_time;
	private Double script_load_time;
	private Double script_parse_time;

	public BootUpTime() {}
	
	public BootUpTime(String url, double total_time, double script_load_time, double script_parse_time) {
		setUrl(url);
		setTotalTime(total_time);
		setScriptLoadTime(script_load_time);
		setScriptParseTime(script_parse_time);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getTotalTime() {
		return total_time;
	}

	public void setTotalTime(Double total_time) {
		this.total_time = total_time;
	}

	public Double getScriptLoadTime() {
		return script_load_time;
	}

	public void setScriptLoadTime(Double script_load_time) {
		this.script_load_time = script_load_time;
	}

	public Double getScriptParseTime() {
		return script_parse_time;
	}

	public void setScriptParseTime(Double script_parse_time) {
		this.script_parse_time = script_parse_time;
	}
}
