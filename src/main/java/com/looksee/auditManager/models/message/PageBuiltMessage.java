package com.looksee.auditManager.models.message;

public class PageBuiltMessage extends Message{
	private long pageId;
	private long pageAuditRecordId;
	
	public PageBuiltMessage() {
		super(-1, -1, -1);
	}
	
	public PageBuiltMessage(long account_id, 
							long domain_audit_id,
							long domain_id,
							long page_id, 
							long page_audit_record_id) 
	{
		super(account_id, domain_audit_id, domain_id);
		setPageId(page_id);
		setPageAuditRecordId(page_audit_record_id);
	}
	
	public long getPageId() {
		return pageId;
	}
	public void setPageId(long page_id) {
		this.pageId = page_id;
	}

	public long getPageAuditRecordId() {
		return pageAuditRecordId;
	}

	public void setPageAuditRecordId(long pageAuditRecordId) {
		this.pageAuditRecordId = pageAuditRecordId;
	}

}
