package com.looksee.auditManager.models;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Relationship;

import com.looksee.auditManager.models.enums.AuditLevel;
import com.looksee.auditManager.models.enums.ExecutionStatus;


/**
 * Record detailing an set of {@link Audit audits}.
 */
public class PageAuditRecord extends AuditRecord {
	@Relationship(type = "HAS")
	private Set<Audit> audits;
	
	private long elementsFound;
	private long elementsReviewed;
	
	public PageAuditRecord() {
		setAudits(new HashSet<>());
		setKey(generateKey());
	}
	
	/**
	 * Constructor
	 * @param audits TODO
	 * @param page_state TODO
	 * @param is_part_of_domain_audit TODO
	 * @param audit_stats {@link AuditStats} object with statics for audit progress
	 * @pre audits != null
	 * @pre page_state != null
	 * @pre status != null;
	 */
	public PageAuditRecord(
			ExecutionStatus status, 
			Set<Audit> audits, 
			boolean is_part_of_domain_audit
	) {
		assert audits != null;
		assert status != null;
		
		setAudits(audits);
		setStatus(status);
		setLevel( AuditLevel.PAGE);
		setKey(generateKey());
	}

	public String generateKey() {
		return "pageauditrecord:"+org.apache.commons.codec.digest.DigestUtils.sha256Hex( System.currentTimeMillis() + " " );
	}

	public Set<Audit> getAudits() {
		return audits;
	}

	public void setAudits(Set<Audit> audits) {
		this.audits = audits;
	}

	public void addAudit(Audit audit) {
		this.audits.add( audit );
	}
	
	public void addAudits(Set<Audit> audits) {
		this.audits.addAll( audits );
	}

	public long getElementsFound() {
		return elementsFound;
	}

	public void setElementsFound(long elements_found) {
		this.elementsFound = elements_found;
	}

	public long getElementsReviewed() {
		return elementsReviewed;
	}

	public void setElementsReviewed(long elements_reviewed) {
		this.elementsReviewed = elements_reviewed;
	}
}
