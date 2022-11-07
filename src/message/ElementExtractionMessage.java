package com.looksee.models.message;

import java.util.List;

import com.looksee.models.PageState;

public class ElementExtractionMessage extends Message{
	private PageState page_state;
	private List<String> xpaths;
	
	public ElementExtractionMessage( long account_id,
									 PageState page,
									 long audit_record_id, 
									 List<String> xpaths, 
									 long domain_id
	) {
		super(domain_id, account_id, audit_record_id);
		setPageState(page);
		setXpaths(xpaths);
	}
	
	public PageState getPageState() {
		return page_state;
	}
	public void setPageState(PageState page_state) {
		this.page_state = page_state;
	}
	public List<String> getXpaths() {
		return xpaths;
	}
	public void setXpaths(List<String> xpaths) {
		this.xpaths = xpaths;
	}
}
