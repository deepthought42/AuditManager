package com.looksee.auditManager.models.message;


/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class DomainAuditUrlMessage extends DomainAuditMessage {
	
	private long pageAuditId;
	private String url;
	
	public DomainAuditUrlMessage() {}
	
	public DomainAuditUrlMessage( long account_id,
				  	   long domain_audit_id,
				  	   String url)
	{
		super(account_id, domain_audit_id);
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
