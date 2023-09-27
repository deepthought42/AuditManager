package com.looksee.auditManager.models.message;

import java.util.Map;

import com.looksee.auditManager.models.enums.AuditStatus;

/**
 * Intended to contain information regarding the progress of an audit 
 *   category such as Content, Information Architecture, etc for a given page
 */
public class AuditProgressMessage extends DomainAuditMessage {
	private long page_audit_id;
	private Map<String, AuditStatus> audit_statuses;
	private Map<String, Float> audit_scores;
	
	public AuditProgressMessage() {	}
	
	public AuditProgressMessage(
			long account_id,
			long audit_record_id,
			Map<String, AuditStatus> audit_statuses,
			Map<String, Float> audit_scores
	) {
		super(account_id, audit_record_id);
		setAuditStatuses(audit_statuses);
		setAuditScores(audit_scores);	}

	/* GETTERS / SETTERS */
	public Map<String, AuditStatus> getAuditStatuses() {
		return audit_statuses;
	}

	public void setAuditStatuses(Map<String, AuditStatus> audit_statuses) {
		this.audit_statuses = audit_statuses;
	}

	public Map<String, Float> getAuditScores() {
		return audit_scores;
	}

	public void setAuditScores(Map<String, Float> audit_scores) {
		this.audit_scores = audit_scores;
	}

	public long getPageAuditId() {
		return page_audit_id;
	}

	public void setPageAuditId(long page_audit_id) {
		this.page_audit_id = page_audit_id;
	}	
}
