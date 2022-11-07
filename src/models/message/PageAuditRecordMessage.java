package com.looksee.models.message;

import com.looksee.models.PageState;

public class PageAuditRecordMessage extends Message {

	private long page_audit_id;
	private PageState page_state;
	
	public PageAuditRecordMessage(long page_audit_id, 
								  long domain_id, 
								  long account_id, 
								  long domain_audit_id, 
								  PageState page_state
	) {
		super(domain_id, account_id, domain_audit_id);
		setPageAuditId(page_audit_id);
		setPageState(page_state);
	}

	public long getPageAuditId() {
		return page_audit_id;
	}

	public void setPageAuditId(long page_audit_id) {
		this.page_audit_id = page_audit_id;
	}

	public PageState getPageState() {
		return page_state;
	}

	public void setPageState(PageState page_state) {
		this.page_state = page_state;
	}
}
