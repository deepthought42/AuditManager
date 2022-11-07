package com.looksee.models.audit.performance;

import java.util.List;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.LookseeObject;
import com.looksee.models.enums.InsightType;

/**
 * Google Page speed audit
 */
public class PageSpeedAudit extends LookseeObject {

	private String name;
	private String title;
	private String description; //definition
	private Double score;      //scoring
	private String score_display_mode;
	private String display_value;
	private String explanation; //meaning
	private String error_message;
	private Double numeric_value;
	private String type;
	
	@Relationship(type = "HAS")
	private List<AuditDetail> details;
	
	public PageSpeedAudit() {
		super();
	}
	
	public PageSpeedAudit(
			String name,
			String description,
			String display_value,
			String error_message,
			String explanation,
			Double numeric_value,
			String score_display_value,
			String title
	) {
		super();
		setName(name);
		setDescription(description);
		setDisplayValue(display_value);
		setErrorMessage(error_message);
		setExplanation(explanation);
		setNumericValue(numeric_value);
		setScoreDisplayMode(score_display_value);
		setTitle(title);
		setKey(generateKey());		
	}
	
	
	@Override
	public String generateKey() {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(getTitle());
	}

	/* GETTERS AND SETTERS */

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getScore() {
		return score;
	}
	
	public void setScore(Double score) {
		this.score = score;
	}
	
	public String getScoreDisplayMode() {
		return score_display_mode;
	}
	
	public void setScoreDisplayMode(String score_display_mode) {
		this.score_display_mode = score_display_mode;
	}
	
	public String getDisplayValue() {
		return display_value;
	}
	
	public void setDisplayValue(String display_value) {
		this.display_value = display_value;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	public String getErrorMessage() {
		return error_message;
	}
	
	public void setErrorMessage(String error_message) {
		this.error_message = error_message;
	}
	
	public Double getNumericValue() {
		return numeric_value;
	}
	
	public void setNumericValue(Double numeric_value) {
		this.numeric_value = numeric_value;
	}

	public InsightType getType() {
		return InsightType.create(type);
	}

	public void setType(InsightType type) {
		this.type = type.getShortName();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<AuditDetail> getDetails() {
		return details;
	}

	public void setDetails(List<AuditDetail> details) {
		this.details = details;
	}
}
