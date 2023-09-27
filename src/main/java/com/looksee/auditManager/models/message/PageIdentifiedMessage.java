package com.looksee.auditManager.models.message;

/**
 * Intended to contain information regarding the identification of a new page 
 *   to be audited as part of a domain {@link DomainAuditRecord audit}.
 */
public class PageIdentifiedMessage extends DomainAuditMessage {
	private long page_id;
	private String url;
	
	public PageIdentifiedMessage() {	}
	
	public PageIdentifiedMessage(
			long account_id,
			long domain_audit_record_id,
			long page_id,
			String url
	) {
		super(account_id, domain_audit_record_id);
		setPageId(page_id);
		setUrl(url);
	}

	/* GETTERS / SETTERS */	
	public long getPageId() {
		return page_id;
	}

	public void setPageId(long page_id) {
		this.page_id = page_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
