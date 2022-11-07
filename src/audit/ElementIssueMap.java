package com.looksee.models.audit;

import java.util.Set;

import com.looksee.models.SimpleElement;

//deprecated on 8-24-2021
@Deprecated
public class ElementIssueMap {
	private Set<UXIssueMessage> issues;
	private SimpleElement element;

	
	public ElementIssueMap(
			Set<UXIssueMessage> issues,
			SimpleElement elements
	) {
		setIssues(issues);
		setElement(elements);
	}


	public Set<UXIssueMessage> getIssues() {
		return issues;
	}

	public void setIssues(Set<UXIssueMessage> issue_messages) {
		this.issues = issue_messages;
	}

	public SimpleElement getElement() {
		return element;
	}

	public void setElement(SimpleElement element) {
		this.element = element;
	}
}
