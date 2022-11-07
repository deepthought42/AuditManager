package com.looksee.browsing.element;

import com.looksee.models.Element;

/**
 * Defines an error element by tag and the string contained within
 */
public class ErrorField {
	private Element tag;
	private String error;
	
	public Element getTag() {
		return tag;
	}
	
	public void setTag(Element tag) {
		this.tag = tag;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
}
