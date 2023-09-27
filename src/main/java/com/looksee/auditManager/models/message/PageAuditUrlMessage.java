package com.looksee.auditManager.models.message;


/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class PageAuditUrlMessage extends PageAuditMessage {
	
	private long pageAuditId;
	private String url;
	
	public PageAuditUrlMessage() {}
	
	public PageAuditUrlMessage( long account_id,
				  	   long page_audit_id,
				  	   String url)
	{
		super(account_id, page_audit_id);
		setUrl(url);
	}

	public long getPageAuditId() {
		return pageAuditId;
	}

	public void setPageAuditId(long page_audit_record_id) {
		this.pageAuditId = page_audit_record_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
