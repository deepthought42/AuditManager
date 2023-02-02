package com.looksee.auditManager.models;

import com.looksee.auditManager.models.enums.AuditPhase;
import com.looksee.auditManager.models.message.Message;

/**
 * Intended to contain information about progress an audit
 */
public class AuditProgressUpdate extends Message {
	private Audit audit;
	private AuditPhase phase;
	private String message;
	
	public AuditProgressUpdate(
			long account_id,
			long domain_id,
			long audit_record_id,
			AuditPhase phase,
			String message			
	) {
		super(account_id, audit_record_id, domain_id);
		setMessage(message);
		setAudit(audit);
	}
	
	/* GETTERS / SETTERS */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Audit getAudit() {
		return audit;
	}

	public void setAudit(Audit audit) {
		this.audit = audit;
	}

	public AuditPhase getPhase() {
		return phase;
	}

	public void setPhase(AuditPhase phase) {
		this.phase = phase;
	}
}
