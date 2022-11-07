package com.looksee.models.audit.performance;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.annotation.Properties;

/**
 * Defines detail item for "dom-size"(Document Object Model size) object in the Google PageSpeed API 
 */
public class DomSize extends AuditDetail {

	private String statistic;
	private String value;
	
	@Properties
	private Map<String, String> element_definition = new HashMap<>();
	
	public DomSize() {}
	
	public DomSize(String statistic, String value, Map<String, String> element_definition) {
		setStatistic(statistic);
		setValue(value);
		setElementDefinition(element_definition);
	}
	
	/** GETTERS AND SETTERS */
	
	public String getStatistic() {
		return statistic;
	}

	public void setStatistic(String statistic) {
		this.statistic = statistic;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getElementDefinition() {
		return element_definition;
	}

	public void setElementDefinition(Map<String, String> element_definition) {
		this.element_definition = element_definition;
	}

}
