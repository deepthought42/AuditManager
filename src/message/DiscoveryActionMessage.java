package com.looksee.models.message;

import com.looksee.models.enums.BrowserType;
import com.looksee.models.enums.DiscoveryAction;

/**
 * 
 * 
 */
public class DiscoveryActionMessage extends Message{
	private DiscoveryAction action;
	private BrowserType browser;
	
	public DiscoveryActionMessage(DiscoveryAction action, long domain_id, long account_id, BrowserType browser){
		setAction(action);
		setDomainId(domain_id);
		setAccountId(account_id);
		setBrowser(browser);
	}
	
	public DiscoveryAction getAction() {
		return action;
	}
	
	private void setAction(DiscoveryAction action) {
		this.action = action;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}
}
