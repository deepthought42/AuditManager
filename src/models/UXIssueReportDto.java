package com.looksee.models;

import java.util.Set;

import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.ObservationType;
import com.looksee.models.enums.Priority;

public class UXIssueReportDto {
	private String title;
	private String description;
	private String why_it_matters;
	private String recommendation;
	private String priority;
	private String type;
	private String category;
	private String wcag_compliance;
	private String element_selector;
	private String page_url;
	
	private Set<String> labels;
	
	public UXIssueReportDto() {}
	
	public UXIssueReportDto(
			String recommendation,
			Priority priority, 
			String description,
			ObservationType type,
			AuditCategory category,
			String wcag_compliance,
			Set<String> labels, 
			String why_it_matters, 
			String title,
			String element_selector,
			String url
	) {
		setRecommendation(recommendation);
		setPriority(priority);
		setDescription(description);
		setType(type);
		setCategory(category);
		setWcagCompliance(wcag_compliance);
		setLabels(labels);
		setWhyItMatters(why_it_matters);
		setElementSelector(element_selector);
		setTitle(title);
		setPageUrl(url);
	}
	
	public Priority getPriority() {
		return Priority.create(this.priority);
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority.getShortName();
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
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

	public String getWcagCompliance() {
		return wcag_compliance;
	}

	public void setWcagCompliance(String wcag_compliance) {
		this.wcag_compliance = wcag_compliance;
	}

	public String getWhyItMatters() {
		return why_it_matters;
	}

	public void setWhyItMatters(String why_it_matters) {
		this.why_it_matters = why_it_matters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getElementSelector() {
		return element_selector;
	}

	public void setElementSelector(String element_selector) {
		this.element_selector = element_selector;
	}

	public String getPageUrl() {
		return page_url;
	}

	public void setPageUrl(String page_url) {
		this.page_url = page_url;
	}
}
