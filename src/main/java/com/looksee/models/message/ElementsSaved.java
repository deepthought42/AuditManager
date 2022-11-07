package com.looksee.models.message;

import java.util.List;

public class ElementsSaved extends Message{
	private String page_url;
	private List<Long> elements;
	private long page_state_id;
	
	public ElementsSaved(long account_id,
						String page_url, 
						long audit_record_id,
						List<Long> element_ids, 
						long page_state_id,
						long domain_id
	) {
		super(domain_id, account_id, audit_record_id);
		setPageUrl(page_url);
		setElements(element_ids);
		setPageStateId(page_state_id);
	}
	
	public String getPageUrl() {
		return page_url;
	}
	
	public void setPageUrl(String page_url) {
		this.page_url = page_url;
	}

	public List<Long> getElements() {
		return elements;
	}

	public void setElements(List<Long> elements) {
		this.elements = elements;
	}

	public long getPageStateId() {
		return page_state_id;
	}

	public void setPageStateId(long page_state_id) {
		this.page_state_id = page_state_id;
	}
}
