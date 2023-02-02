package com.looksee.auditManager.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.Relationship;

import com.looksee.auditManager.models.enums.AuditLevel;
import com.looksee.auditManager.models.enums.ExecutionStatus;


/**
 * Record detailing an set of {@link Audit audits}.
 */
public class DomainAuditRecord extends AuditRecord {
	private int totalPages;
	
	@Relationship(type = "HAS")
	private Set<PageAuditRecord> pageAuditRecords;
	
	public DomainAuditRecord() {
		super();
		setAudits(new HashSet<>());
	}
	
	/**
	 * Constructor
	 * 
	 * @param audit_stats {@link AuditStats} object with statics for audit progress
	 * @param level TODO
	 * 
	 * @pre audit_stats != null;
	 */
	public DomainAuditRecord(ExecutionStatus status) {
		super();
		assert status != null;
		
		setAudits(new HashSet<>());
		setStatus(status);
		setLevel( AuditLevel.DOMAIN);
		setStartTime(LocalDateTime.now());
		setAestheticAuditProgress(0.0);
		setContentAuditProgress(0.0);
		setInfoArchitectureAuditProgress(0.0);
		setDataExtractionProgress(0.0);
		setTotalPages(0);
		setKey(generateKey());
	}

	public String generateKey() {
		return "domainauditrecord:"+UUID.randomUUID().toString()+org.apache.commons.codec.digest.DigestUtils.sha256Hex(System.currentTimeMillis() + "");
	}

	public Set<PageAuditRecord> getAudits() {
		return pageAuditRecords;
	}

	public void setAudits(Set<PageAuditRecord> audits) {
		this.pageAuditRecords = audits;
	}

	public void addAudit(PageAuditRecord audit) {
		this.pageAuditRecords.add( audit );
	}
	
	public void addAudits(Set<PageAuditRecord> audits) {
		this.pageAuditRecords.addAll( audits );
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int total_pages) {
		this.totalPages = total_pages;
	}
}
