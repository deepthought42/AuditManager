package com.looksee.auditManager.models.message;

/**
 * Used to initiate audits of {@link PageState}
 */
public class PageAuditRunMessage extends Message {
	private long page_audit_id;
	private long page_id;
	
	public PageAuditRunMessage() {}
	
	public PageAuditRunMessage(long account_id,
							long page_audit_id, 
							long page_id
	) {
		super(account_id);
		setPageAuditId(page_audit_id);
	}

	public long getPageAuditId() {
		return page_audit_id;
	}

	public void setPageAuditId(long page_audit_id) {
		this.page_audit_id = page_audit_id;
	}

	public long getPageId() {
		return page_id;
	}

	public void setPageId(long page_id) {
		this.page_id = page_id;
	}
}
