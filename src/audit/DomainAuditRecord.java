package com.looksee.models.audit;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.Domain;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.ExecutionStatus;
import com.looksee.models.journeys.Journey;

/**
 * Record detailing an set of {@link Audit audits}.
 */
public class DomainAuditRecord extends AuditRecord {
	private int total_pages;
	
	@Relationship(type = "HAS")
	private Set<PageAuditRecord> page_audit_records;
	
	@Relationship(type = "BELONGS_TO")
	private Domain domain;
	
	@Relationship(type = "HAS_PATH")
	private Journey journey;
	
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
		return page_audit_records;
	}

	public void setAudits(Set<PageAuditRecord> audits) {
		this.page_audit_records = audits;
	}

	public void addAudit(PageAuditRecord audit) {
		this.page_audit_records.add( audit );
	}
	
	public void addAudits(Set<PageAuditRecord> audits) {
		this.page_audit_records.addAll( audits );
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public int getTotalPages() {
		return total_pages;
	}

	public void setTotalPages(int total_pages) {
		this.total_pages = total_pages;
	}
}
