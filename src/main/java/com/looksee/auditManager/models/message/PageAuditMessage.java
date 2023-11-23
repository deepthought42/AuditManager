package com.looksee.auditManager.models.message;

public class PageAuditMessage extends Message {
	private long pageAuditId;
	
	public PageAuditMessage() {}
	
	public PageAuditMessage(long account_id,
							long page_audit_id
	) {
		super(account_id);
		setPageAuditId(page_audit_id);
	}

	public long getPageAuditId() {
		return pageAuditId;
	}

	public void setPageAuditId(long page_audit_id) {
		this.pageAuditId = page_audit_id;
	}
}
