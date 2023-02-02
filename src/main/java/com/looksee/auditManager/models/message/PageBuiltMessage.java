package com.looksee.auditManager.models.message;


public class PageBuiltMessage extends Message{
	private long page_id;
	private long page_audit_id;
	
	public PageBuiltMessage() {
		super(-1, -1, -1);
	}
	
	public PageBuiltMessage(long account_id, 
							long domain_audit_id,
							long domain_id,
							long page_id, 
							long page_audit_id) 
	{
		super(account_id, domain_audit_id, domain_id);
		setPageId(page_id);
		setPageAuditId(page_audit_id);
	}
	
	public long getPageId() {
		return page_id;
	}
	public void setPageId(long page_id) {
		this.page_id = page_id;
	}

	public long getPageAuditId() {
		return page_audit_id;
	}

	public void setPageAuditId(long page_audit_id) {
		this.page_audit_id = page_audit_id;
	}
}
