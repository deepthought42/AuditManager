package com.looksee.models.message;

import com.looksee.models.enums.BrowserType;

public class DiscardedJourneyMessage {

	private int id;
	private BrowserType browserType;
	private long domainId;
	private long accountId;
	private long auditRecordId;
   
	public DiscardedJourneyMessage(int id, 
								   BrowserType browserType, 
								   long domainId, 
								   long accountId, 
								   long auditRecordId) {
		setId(id);
		setBrowserType(browserType);
		setDomainId(domainId);
		setAccountId(accountId);
		setAuditRecordId(auditRecordId);
	}

	public BrowserType getBrowserType() {
		return browserType;
	}

	public void setBrowserType(BrowserType browserType) {
		this.browserType = browserType;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getAuditRecordId() {
		return auditRecordId;
	}

	public void setAuditRecordId(long auditRecordId) {
		this.auditRecordId = auditRecordId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
