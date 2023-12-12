package com.looksee.auditManager.models.message;

/**
 * Intended to contain information about progress an audit
 */
public class AuditProgressUpdate extends Message {
	private long audit_record_id;
	private String message;
	
	public AuditProgressUpdate() {	}
	
	public AuditProgressUpdate(
			long account_id,
			long audit_record_id,
			String message
	) {
		super(account_id);
		setMessage(message);
		setAuditRecordId(audit_record_id);
	}
	
	/* GETTERS / SETTERS */
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public long getAuditRecordId() {
		return audit_record_id;
	}

	public void setAuditRecordId(long audit_record_id) {
		this.audit_record_id = audit_record_id;
	}
}

