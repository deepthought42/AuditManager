package com.looksee.models.message;

import com.looksee.models.Domain;

public class DiscoveryActionRequest {
	private Domain domain;
	private String account;
	
	public DiscoveryActionRequest(Domain domain, String account_id) {
		this.setDomain(domain);
		this.setAccountId(account_id);
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public String getAccountId() {
		return account;
	}

	public void setAccountId(String account_id) {
		this.account = account_id;
	}
}
