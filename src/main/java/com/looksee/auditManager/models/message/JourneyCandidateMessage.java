package com.looksee.auditManager.models.message;

import java.util.ArrayList;
import java.util.List;

import com.looksee.auditManager.models.enums.BrowserType;
import com.looksee.auditManager.models.journeys.Step;


/**
 * 
 */
public class JourneyCandidateMessage extends Message {
	private List<Step> steps;
	private BrowserType browser;
	
	public JourneyCandidateMessage() {}
	
	public JourneyCandidateMessage(List<Step> steps, 
						   BrowserType browser_type, 
						   long domain_id, 
						   long account_id, 
						   long audit_record_id)
	{
		super(account_id, audit_record_id, domain_id);
		setSteps(steps);
		setBrowser(browser_type);
	}

	public JourneyCandidateMessage clone(){
		return new JourneyCandidateMessage(new ArrayList<Step>(steps), 
								  getBrowser(), 
								  getDomainId(),
								  getAccountId(), 
								  getDomainAuditRecordId());
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	public List<Step> getSteps() {
		return this.steps;
	}
	
}
