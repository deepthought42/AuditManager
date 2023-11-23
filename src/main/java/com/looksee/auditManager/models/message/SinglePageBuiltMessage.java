package com.looksee.auditManager.models.message;

public class SinglePageBuiltMessage extends PageAuditMessage{
	private long pageId;
	
	public SinglePageBuiltMessage() {}
	
	public SinglePageBuiltMessage(long account_id, 
							long page_id,
							long page_audit_record_id) 
	{
		super(account_id, page_audit_record_id);
		setPageId(page_id);
	}
	
	public long getPageId() {
		return pageId;
	}
	public void setPageId(long page_id) {
		this.pageId = page_id;
	}
}

