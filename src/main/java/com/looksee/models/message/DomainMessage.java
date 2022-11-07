package com.looksee.models.message;

import com.looksee.models.Domain;

/**
 * Message containing a website's domain and a raw URL within the domain. Will be checked if it is in a correct format and has a valid URL call to be prepared for proper source extraction.
 */
public class DomainMessage extends Message {
	private Domain domain;
	private String raw_url;

	public DomainMessage(Message crawl_action,
						 Domain domain,
						 String raw_url){
		
		super(crawl_action.getDomainId(),
			  crawl_action.getAccountId(),
			  crawl_action.getAuditRecordId());
		
		setDomain(domain);
		setRawUrl(raw_url);
	}

	public Domain getDomain(){
		return this.domain;
	}

	public void setDomain(Domain domain){
		this.domain = domain;
	}

	public String getRawUrl(){
		return this.raw_url;
	}

	public void setRawUrl(String raw_url){
		this.raw_url = raw_url;
	}
}
