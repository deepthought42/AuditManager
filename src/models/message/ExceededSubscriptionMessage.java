package com.looksee.models.message;

public class ExceededSubscriptionMessage extends Message{

	
	public ExceededSubscriptionMessage(long accountId, long domainId, long auditRecordId) {
		setAccountId(accountId);
		setDomainId(domainId);
		setAuditRecordId(auditRecordId);
	}
	
}
