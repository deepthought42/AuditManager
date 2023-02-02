package com.looksee.auditManager.models;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.ObservationType;
import com.looksee.auditManager.models.enums.Priority;


public class UXIssueMessage extends LookseeObject {
	private static Logger log = LoggerFactory.getLogger(UXIssueMessage.class);

	private String title;
	private String description;
	private String whyItMatters;
	private String recommendation;
	private String priority;
	private String type;
	private String category;
	private String wcagCompliance;
	private Set<String> labels;
	private int points;
	private int maxPoints;
	private int score;

	public UXIssueMessage() {}
	
	public UXIssueMessage(
			Priority priority,
			String description, 
			ObservationType type,
			AuditCategory category,
			String wcag_compliance,
			Set<String> labels,
			String why_it_matters, 
			String title, 
			int points, 
			int max_points, 
			String recommendation
	) {
		assert priority != null;
		assert category != null;
		assert labels != null;

		setPriority(priority);
		setDescription(description);
		setType(type);
		setCategory(category);
		setRecommendation(recommendation);
		setWcagCompliance(wcag_compliance);
		setLabels(labels);
		setWhyItMatters(why_it_matters);
		setTitle(title);
		setPoints(points);
		setMaxPoints(max_points);
		setScore( (int)((points/(double)max_points)*100) );
		setKey(generateKey());
	}
	
	public void print() {
		log.warn("ux issue key :: "+getKey());
		log.warn("ux issue desc :: "+getDescription());
		log.warn("ux issue points :: "+getPoints());
		log.warn("ux issue max point :: "+getMaxPoints());
		log.warn("ux issue reco :: "+getRecommendation());
		log.warn("ux issue score :: "+getScore());
		log.warn("ux issue title ::"+ getTitle());
		log.warn("ux issue wcag :: "+getWcagCompliance());
		log.warn("ux issue why it matters :: "+getWhyItMatters());
		log.warn("ux issue category :: "+getCategory());
		log.warn("ux issue labels:: "+getLabels());
		log.warn("ux issue priority :: "+getPriority());
		log.warn("ux issue type :: "+getType());
		log.warn("------------------------------------------------------------------------------");
		
	}
	
	public Priority getPriority() {
		return Priority.create(this.priority);
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority.getShortName();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ObservationType getType() {
		return ObservationType.create(type);
	}

	public void setType(ObservationType type) {
		this.type = type.getShortName();
	}

	public AuditCategory getCategory() {
		return AuditCategory.create(category);
	}

	public void setCategory(AuditCategory category) {
		this.category = category.getShortName();
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}

	@Override
	public String generateKey() {
		return "issuemessage"+UUID.randomUUID();
	}

	public String getWcagCompliance() {
		return wcagCompliance;
	}

	public void setWcagCompliance(String wcag_compliance) {
		this.wcagCompliance = wcag_compliance;
	}

	public String getWhyItMatters() {
		return whyItMatters;
	}

	public void setWhyItMatters(String why_it_matters) {
		this.whyItMatters = why_it_matters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int max_points) {
		this.maxPoints = max_points;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
