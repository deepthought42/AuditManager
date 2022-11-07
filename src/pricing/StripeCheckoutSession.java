package com.looksee.models.pricing;

public class StripeCheckoutSession {
	private String url;

	public StripeCheckoutSession(String url) {
		setUrl(url);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
