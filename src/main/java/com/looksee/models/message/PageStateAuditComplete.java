package com.looksee.models.message;

import com.looksee.models.PageState;

public class PageStateAuditComplete extends Message {

	private PageState page_state;
	
	public PageStateAuditComplete(long account_id, 
								  long domain_id, 
								  long audit_record_id, 
								  PageState page_state
	) {
		super(domain_id, account_id, audit_record_id);
		setPageState(page_state);
	}

	public PageState getPageState() {
		return page_state;
	}

	public void setPageState(PageState page_state) {
		this.page_state = page_state;
	}
}
