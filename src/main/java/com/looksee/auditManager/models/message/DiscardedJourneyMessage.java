package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.enums.BrowserType;

public class DiscardedJourneyMessage {

	private long id;
	private BrowserType browserType;
	private long domainId;
	private long accountId;
	private long auditRecordId;
   
	public DiscardedJourneyMessage(long journey_id, 
								   BrowserType browserType, 
								   long domainId, 
								   long accountId, 
								   long auditRecordId) {
		setId(journey_id);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
