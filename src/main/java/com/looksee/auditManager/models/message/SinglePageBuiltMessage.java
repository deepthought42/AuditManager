package com.looksee.auditManager.models.message;

public class SinglePageBuiltMessage extends DomainAuditMessage{
	private long pageId;
	private long pageAuditRecordId;
	
	public SinglePageBuiltMessage() {}
	
	public SinglePageBuiltMessage(long account_id, 
							long domain_audit_id,
							long page_id,
							long page_audit_record_id) 
	{
		super(account_id, domain_audit_id);
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
