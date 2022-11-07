package com.looksee.models;

import java.util.Set;

import com.looksee.models.audit.Audit;

public class PageStateAudits {
	
	private SimplePage page;
	private Set<Audit> audits;
	
	public PageStateAudits(SimplePage page, Set<Audit> audits) {
		setPage(page);
		setAudits(audits);
	}

	public Set<Audit> getAudits() {
		return audits;
	}

	public void setAudits(Set<Audit> audits) {
		this.audits = audits;
	}

	public SimplePage getPage() {
		return page;
	}

	public void setPage(SimplePage page) {
		this.page = page;
	}

}
