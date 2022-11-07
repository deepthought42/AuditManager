package com.looksee.models.message;

import java.util.List;

import com.looksee.models.enums.BrowserType;
import com.looksee.models.enums.PathStatus;
import com.looksee.models.journeys.Step;
import com.looksee.utils.ListUtils;

public class JourneyMessage extends Message {

	private int id;
	private List<Step> steps;
	private PathStatus status;
	private BrowserType browser;
	
	public JourneyMessage(int id,
					   List<Step> steps, 
					   PathStatus status, 
					   BrowserType browser_type, 
					   long domain_id, 
					   long account_id, long audit_record_id){
		super(domain_id, account_id, audit_record_id);
		setId(id);
		setSteps(steps);
		setStatus(status);
		setBrowser(browser_type);
	}
	
	public JourneyMessage clone(){
		return new JourneyMessage(getId(), 
								  ListUtils.clone(getSteps()), 
								  getStatus(), 
								  getBrowser(), 
								  getDomainId(),
								  getAccountId(), getAuditRecordId());
	}

	public PathStatus getStatus() {
		return status;
	}

	private void setStatus(PathStatus status) {
		this.status = status;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
