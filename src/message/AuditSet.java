package com.looksee.models.message;

import java.util.List;

import com.looksee.models.PageState;
import com.looksee.models.audit.Audit;

/**
 * Message that contains a {@link PageState} that is ready for analysis
 * 
 */
public class AuditSet extends Message {
	private List<Audit> audits;
	private String url;
	

	public AuditSet(long account_id, List<Audit> audits, String url){
		setAccountId(account_id);
		setAudits(audits);
		setUrl(url);
	}

	public List<Audit> getAudits() {
		return audits;
	}

	public void setAudits(List<Audit> audits) {
		this.audits = audits;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
