package com.looksee.auditManager.models.message;

/**
 * Intended to contain information regarding the progress of journey 
 *   mapping for a domain audit.
 */
public class JourneyMappingProgressMessage extends DomainAuditMessage {
	private int candidate_count;
	private int completed_count;
	
	public JourneyMappingProgressMessage() {	}
	
	public JourneyMappingProgressMessage(
			long account_id,
			long audit_record_id,
			int candidate_count,
			int completed_count
	) {
		super(account_id, audit_record_id);
		setCandidateCount(candidate_count);
		setCompletedCount(completed_count);
	}

	/* GETTERS / SETTERS */	
	public int getCandidateCount() {
		return candidate_count;
	}

	public void setCandidateCount(int candidate_count) {
		this.candidate_count = candidate_count;
	}

	public int getCompletedCount() {
		return completed_count;
	}

	public void setCompletedCount(int completed_count) {
		this.completed_count = completed_count;
	}
}
