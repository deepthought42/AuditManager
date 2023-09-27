package com.looksee.auditManager.models.message;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.AuditLevel;

/**
 * Intended to contain information about progress an audit
 */
public class PageAuditProgressMessage extends PageAuditMessage {
	private AuditCategory category;
	private AuditLevel level;
	private double progress;
	private String message;
	
	public PageAuditProgressMessage() {	}
	
	public PageAuditProgressMessage(
			long account_id,
			long audit_record_id,
			double progress,
			String message, 
			AuditCategory category,
			AuditLevel level
	) {
		super(account_id, audit_record_id);
		setProgress(progress);
		setMessage(message);
		setCategory(category);
		setLevel(level);
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
}
