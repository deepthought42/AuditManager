package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.enums.BrowserType;
import com.looksee.auditManager.models.enums.JourneyStatus;
import com.looksee.auditManager.models.journeys.Journey;

/**
 * 
 */
public class VerifiedJourneyMessage extends Message {

	private Journey journey;
	private JourneyStatus status;
	private BrowserType browser;
	
	public VerifiedJourneyMessage() {}
	
	public VerifiedJourneyMessage( Journey journey, 
								   JourneyStatus status, 
								   BrowserType browser,
								   long domain_id,
								   long account_id, 
								   long audit_record_id)
	{
		setJourney(journey);
		setStatus(status);
		setBrowser(browser);
		setDomainId(domain_id);
		setAccountId(account_id);
		setDomainAuditRecordId(audit_record_id);
	}
	
	public VerifiedJourneyMessage clone(){
		return new VerifiedJourneyMessage(	journey.clone(), 
											getStatus(), 
											getBrowser(), 
											getDomainId(), 
											getAccountId(), 
											getDomainAuditRecordId());
	}

	public JourneyStatus getStatus() {
		return status;
	}

	private void setStatus(JourneyStatus status) {
		this.status = status;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public Journey getJourney() {
		return journey;
	}

	public void setJourney(Journey journey) {
		this.journey = journey;
	}
}

