package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.enums.BrowserType;
import com.looksee.auditManager.models.enums.JourneyStatus;
import com.looksee.auditManager.models.journeys.Journey;


public class JourneyMessage extends Message {

	private Journey journey;
	private JourneyStatus status;
	private BrowserType browser;
	
	public JourneyMessage( Journey journey, 
						   JourneyStatus status, 
						   BrowserType browser_type, 
						   long domain_id, 
						   long account_id, 
						   long audit_record_id){
		super(domain_id, account_id, audit_record_id);
		setJourney(journey);
		setStatus(status);
		setBrowser(browser_type);
	}
	
	public JourneyMessage clone(){
		return new JourneyMessage(journey.clone(),
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

	public void setJourney(Journey journey) {
		this.journey = journey;
	}
	
	public Journey getJourney() {
		return this.journey;
	}
}
