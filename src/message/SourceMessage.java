package com.looksee.models.message;

import java.net.URL;

/**
 * A message that contains a sanitized url and its page source that will be further extracted for any links it contains.
 */
public class SourceMessage extends Message {
	private URL url;
	private String page_src;

	public SourceMessage(Message crawl_action,
					   URL sanitized_url,
					   String page_src){
		
		super(crawl_action.getDomainId(),
			  crawl_action.getAccountId(),
			  crawl_action.getAuditRecordId());
		
		setUrl(sanitized_url);
		setPageSrc(page_src);
	}

	public URL getUrl(){
		return this.url;
	}

	public void setUrl(URL url){
		this.url = url;
	}

	public String getPageSrc(){
		return page_src;
	}

	public void setPageSrc(String page_src){
		this.page_src = page_src;
	}
}
