package com.looksee.models.message;

import com.looksee.models.enums.AuditCategory;

public class AuditError extends Message{
	private String error_message;
	private AuditCategory audit_category;
	private double progress;
	
	public AuditError(long domainId, 
					  long accountId, 
					  long auditRecordId, 
					  String error_message,
					  AuditCategory category, 
					  double progress) {
		super(domainId, accountId, auditRecordId);
		setErrorMessage(error_message);
		setAuditCategory(category);
		setProgress(progress);
	}

	public String getErrorMessage() {
		return error_message;
	}

	public void setErrorMessage(String error_message) {
		this.error_message = error_message;
	}

	public AuditCategory getAuditCategory() {
		return audit_category;
	}

	public void setAuditCategory(AuditCategory audit_category) {
		this.audit_category = audit_category;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

}
