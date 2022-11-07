package com.looksee.models.competitiveanalysis;

/**
 * Core Message object that defines global fields that are to be used by all Message objects
 */
public abstract class Message {
	private long competitor_id;
	private long account_id;
	private long audit_record_id;
	
	public Message(){
		setAccountId(-1);
	}
	
	/**
	 * 
	 * @param account_id
	 * @param audit_record_id TODO
	 * @param domain eg. example.com
	 */
	public Message(long competitor_id, long account_id, long audit_record_id){
		setCompetitorId(competitor_id);
		setAccountId(account_id);
		setAuditRecordId(audit_record_id);
	}
	
	public long getCompetitorId() {
		return competitor_id;
	}
	
	private void setCompetitorId(long competitor_id) {
		this.competitor_id = competitor_id;
	}
	
	public long getAccountId() {
		return account_id;
	}

	protected void setAccountId(long account_id) {
		this.account_id = account_id;
	}

	public long getAuditRecordId() {
		return audit_record_id;
	}

	public void setAuditRecordId(long audit_record_id) {
		this.audit_record_id = audit_record_id;
	}
}
