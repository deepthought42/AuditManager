package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.Audit;

/**
 * 
 * 
 */
public class AuditMessage extends Message {

	private Audit audit;
	private double audit_progress;
	private long page_audit_record_id;
	
	public AuditMessage( Audit audit,
						 double audit_progress,
						 long account_id, 
						 long page_audit_record_id)
	{
		super(account_id);
		setAudit(audit);
		setAuditProgress(audit_progress);
		setPageAuditRecordId(page_audit_record_id);
	}
	
	public AuditMessage clone(){
		return new AuditMessage(  audit.clone(),
								  getAuditProgress(),
								  getAccountId(),
								  getPageAuditRecordId());
	}

	
	public void setAudit(Audit audit) {
		this.audit = audit;
	}
	
	public Audit getAudit() {
		return this.audit;
	}

	public long getPageAuditRecordId() {
		return page_audit_record_id;
	}

	public void setPageAuditRecordId(long page_audit_record_id) {
		this.page_audit_record_id = page_audit_record_id;
	}

	public double getAuditProgress() {
		return audit_progress;
	}

	public void setAuditProgress(double audit_progress) {
		this.audit_progress = audit_progress;
	}
}
