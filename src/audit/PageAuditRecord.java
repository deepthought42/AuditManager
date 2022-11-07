package com.looksee.models.audit;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.PageState;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.ExecutionStatus;

/**
 * Record detailing an set of {@link Audit audits}.
 */
public class PageAuditRecord extends AuditRecord {
	@Relationship(type = "HAS")
	private Set<Audit> audits;
	
	@Relationship(type = "FOR")
	private PageState page_state;
	
	private String status;
	private long elements_found;
	private long elements_reviewed;
	
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
			PageState page_state, 
			boolean is_part_of_domain_audit
	) {
		assert audits != null;
		assert status != null;
		
		setAudits(audits);
		setPageState(page_state);
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

	public ExecutionStatus getStatus() {
		return ExecutionStatus.create(status);
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status.getShortName();
	}

	public PageState getPageState() {
		return page_state;
	}

	public void setPageState(PageState page_state) {
		this.page_state = page_state;
	}

	public long getElementsFound() {
		return elements_found;
	}

	public void setElementsFound(long elements_found) {
		this.elements_found = elements_found;
	}

	public long getElementsReviewed() {
		return elements_reviewed;
	}

	public void setElementsReviewed(long elements_reviewed) {
		this.elements_reviewed = elements_reviewed;
	}
}
