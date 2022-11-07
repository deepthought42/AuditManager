package com.looksee.models.audit;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.looksee.models.SimpleElement;

public class ElementIssueTwoWayMapping {
	private Collection<? extends UXIssueMessage> issues;
	private Collection<SimpleElement> elements;
	
	private Map<String, String> issues_element_map; // 1 to 1 correlation
	private Map<String, Set<String>> element_issues; // 1 to many correlation
	
	private AuditScore scores;
	private String page_src;
	
	public ElementIssueTwoWayMapping(
			Collection<? extends UXIssueMessage> issues,
			Collection<SimpleElement> elements,
			Map<String, String> issue_element_map,
			Map<String, Set<String>> element_issues, 
			AuditScore audit_score, 
			String page_src
	) {
		setIssues(issues);
		setElements(elements);
		setIssueElementMap(issue_element_map);
		setElementIssues(element_issues);
		setScores(audit_score);
		setPageSrc(page_src);
	}


	public Collection<? extends UXIssueMessage> getIssues() {
		return issues;
	}


	public void setIssues(Collection<? extends UXIssueMessage> issues) {
		this.issues = issues;
	}


	public Collection<SimpleElement> getElements() {
		return elements;
	}


	public void setElements(Collection<SimpleElement> elements) {
		this.elements = elements;
	}


	public AuditScore getScores() {
		return scores;
	}


	public void setScores(AuditScore audit_score) {
		this.scores = audit_score;
	}


	public String getPageSrc() {
		return page_src;
	}


	public void setPageSrc(String page_src) {
		this.page_src = page_src;
	}


	public Map<String, String> getIssueElementMap() {
		return issues_element_map;
	}


	public void setIssueElementMap(Map<String, String> issues_element_map) {
		this.issues_element_map = issues_element_map;
	}


	public Map<String, Set<String>> getElementIssues() {
		return element_issues;
	}


	public void setElementIssues(Map<String, Set<String>> element_issues) {
		this.element_issues = element_issues;
	}

}
