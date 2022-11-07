package com.looksee.models.message;

import java.net.URL;

import com.looksee.browsing.Browser;
import com.looksee.models.enums.CrawlAction;

/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class BrowserCrawlActionMessage extends Message {
	private CrawlAction action;
	private URL url;
	private Browser browser;
	
	public BrowserCrawlActionMessage( long domain_id, 
									  long account_id, 
									  long record_id, 
									  Browser browser
	){
		super(domain_id, account_id, record_id);
		
		setAction(action);
		setBrowser(browser);
	}
	
	public CrawlAction getAction() {
		return action;
	}
	
	private void setAction(CrawlAction action) {
		this.action = action;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}	
}
