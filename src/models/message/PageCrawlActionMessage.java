package com.looksee.models.message;

import java.net.URL;

import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.enums.CrawlAction;

/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class PageCrawlActionMessage extends Message{
	private CrawlAction action;
	private PageAuditRecord audit_record;
	private URL url;
		
	public PageCrawlActionMessage(CrawlAction action, 
							  long domain_id, 
							  long account_id, 
							  PageAuditRecord record, 
							  URL url){
		super(domain_id, account_id, record.getId());
		setAction(action);
		setUrl(url);
		setAuditRecord(record);
	}
	
	public CrawlAction getAction() {
		return action;
	}
	
	private void setAction(CrawlAction action) {
		this.action = action;
	}

	public PageAuditRecord getAuditRecord() {
		return audit_record;
	}

	public void setAuditRecord(PageAuditRecord audit_record) {
		this.audit_record = audit_record;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}	
}
