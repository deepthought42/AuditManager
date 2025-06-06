package com.looksee.auditManager.models.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PageBuiltMessage extends Message{
	@Getter
	@Setter
	private long pageId;
	
	@Getter
	@Setter
	private long auditRecordId;
	
	public PageBuiltMessage(long account_id,
							long page_id,
							long audit_record_id)
	{
		super(account_id);
		setPageId(page_id);
		setAuditRecordId(audit_record_id);
	}
}

