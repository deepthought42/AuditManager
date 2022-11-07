package com.looksee.models.message;

import java.net.URL;

import com.looksee.models.enums.BrowserType;


public class UrlMessage extends Message{
	private URL url;
	private BrowserType browser;
	
	public UrlMessage(URL url, 
					  BrowserType browser, 
					  long domain_id, 
					  long account_id){
		setUrl(url);
		setBrowser(browser);
		setDomainId(domain_id);
		setAccountId(account_id);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	private void setBrowser(BrowserType browser) {
		this.browser = browser;
	}
}
