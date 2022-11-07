package com.looksee.models.message;

/**
 * Core Message object that defines global fields that are to be used by all Message objects
 */
public abstract class Message {
	private long domain_id;
	private long account_id;
	private long audit_record_id;
	
	Message(){
		setAccountId(-1);
	}
	
	/**
	 * 
	 * @param account_id
	 * @param audit_record_id TODO
	 * @param domain eg. example.com
	 */
	Message(long domain_id, long account_id, long audit_record_id){
		setDomainId(domain_id);
		setAccountId(account_id);
		setAuditRecordId(audit_record_id);
	}
	
	public long getDomainId() {
		return domain_id;
	}
	
	protected void setDomainId(long domain) {
		this.domain_id = domain;
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
