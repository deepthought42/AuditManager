package com.looksee.models.message;

import java.net.URL;

import com.looksee.models.enums.CrawlAction;

/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class CrawlActionMessage extends Message {
	private CrawlAction action;
	private boolean is_individual;
	private URL url;
	private String host;
	
	public CrawlActionMessage(CrawlAction action, 
							  long domain_id, 
							  long account_id, 
							  long record_id, 
							  boolean is_individual,
							  URL url,
							  String host){
		super(domain_id, account_id, record_id);
		
		setAction(action);
		setIsIndividual(is_individual);
		setUrl(url);
		setHost(host);
	}
	
	public CrawlAction getAction() {
		return action;
	}
	
	private void setAction(CrawlAction action) {
		this.action = action;
	}

	public boolean isIndividual() {
		return is_individual;
	}

	public void setIsIndividual(boolean is_individual) {
		this.is_individual = is_individual;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}	
}
