package com.looksee.models.message;

import com.looksee.models.audit.DomainAuditRecord;

public class DomainAuditRecordMessage extends Message {

	private DomainAuditRecord domain_audit;
	
	public DomainAuditRecordMessage(DomainAuditRecord domain_audit, 
								  long domain_id, 
								  long account_id, 
								  long domain_audit_id
	) {
		super(domain_id, account_id, domain_audit_id);
		setDomainAuditRecord(domain_audit);
	}

	public DomainAuditRecord getDomainAuditRecord() {
		return domain_audit;
	}

	public void setDomainAuditRecord(DomainAuditRecord domain_audit) {
		this.domain_audit = domain_audit;
	}
}
