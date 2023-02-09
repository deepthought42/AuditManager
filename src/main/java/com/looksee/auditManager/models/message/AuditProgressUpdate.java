package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.AuditLevel;

/**
 * Intended to contain information about progress an audit
 */
public class AuditProgressUpdate extends Message {
	private long pageAuditId;
	private AuditCategory category;
	private AuditLevel level;
	private double progress;
	private String message;
	
	public AuditProgressUpdate() {	}
	
	public AuditProgressUpdate(
			long account_id,
			long audit_record_id,
			double progress,
			String message, 
			AuditCategory category,
			AuditLevel level, 
			long domain_id, 
			long page_audit_id
	) {
		super(account_id, audit_record_id, domain_id);
		setProgress(progress);
		setMessage(message);
		setCategory(category);
		setLevel(level);
		setPageAuditId(page_audit_id);
	}
	
	/* GETTERS / SETTERS */
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory audit_category) {
		this.category = audit_category;
	}

	public AuditLevel getLevel() {
		return level;
	}

	public void setLevel(AuditLevel level) {
		this.level = level;
	}

	public long getPageAuditId() {
		return pageAuditId;
	}

	public void setPageAuditId(long page_audit_id) {
		this.pageAuditId = page_audit_id;
	}
}
