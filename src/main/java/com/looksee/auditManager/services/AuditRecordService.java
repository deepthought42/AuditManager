package com.looksee.auditManager.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.ExecutionStatus;
import com.looksee.auditManager.models.repository.AuditRecordRepository;
import com.looksee.auditManager.models.repository.AuditRepository;
import com.looksee.auditManager.services.AuditRecordService;
import com.looksee.auditManager.models.Audit;
import com.looksee.auditManager.models.AuditRecord;
import com.looksee.auditManager.models.DesignSystem;
import com.looksee.auditManager.models.DomainAuditRecord;
import com.looksee.auditManager.models.Label;
import com.looksee.auditManager.models.PageAuditRecord;
import com.looksee.auditManager.models.PageState;
import com.looksee.auditManager.models.UXIssueMessage;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * Contains business logic for interacting with and managing audits
 *
 */
@Service
@Retry(name = "neoforj")
public class AuditRecordService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuditRecordService.class);
	
	@Autowired
	private AuditRecordRepository audit_record_repo;
	
	@Autowired
	private AuditRepository audit_repo;
	
	@Autowired
	private PageStateService page_state_service;
	
	public AuditRecord save(AuditRecord audit) {
		assert audit != null;

		return audit_record_repo.save(audit);
	}
	
	public AuditRecord save(AuditRecord audit, Long account_id, Long domain_id) {
		assert audit != null;

		AuditRecord audit_record = audit_record_repo.save(audit);
		
		/*
		if(audit instanceof DomainAuditRecord 
				&& account_id != null 
				&& account_id >= 0 
				&& domain_id != null 
				&& domain_id >= 0) 
		{
			try {
				Account account = account_service.findById(account_id).get();
				int id_start_idx = account.getUserId().indexOf('|');
				String user_id = account.getUserId().substring(id_start_idx+1);
				Domain domain = domain_service.findById(domain_id).get();
				DomainDto domain_dto = domain_dto_service.build(domain);
				MessageBroadcaster.sendAuditRecord(user_id, domain_dto);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
		//broadcast audit record to users
		return audit_record;
	}

	public Optional<AuditRecord> findById(long id) {
		return audit_record_repo.findById(id);
	}
	
	public AuditRecord findByKey(String key) {
		return audit_record_repo.findByKey(key);
	}


	public List<AuditRecord> findAll() {
		// TODO Auto-generated method stub
		return IterableUtils.toList(audit_record_repo.findAll());
	}
	
	public void addAudit(String audit_record_key, String audit_key) {
		//check if audit already exists for page state
		Optional<Audit> audit = audit_repo.getAuditForAuditRecord(audit_record_key, audit_key);
		if(!audit.isPresent()) {
			audit_record_repo.addAudit(audit_record_key, audit_key);
		}
	}

	public void addAudit(long audit_record_id, long audit_id) {
		assert audit_record_id != audit_id;
		
		//check if audit already exists for page state
		audit_record_repo.addAudit(audit_record_id, audit_id);
	}
	
	public Set<Audit> getAllAuditsAndIssues(long audit_id) {		
		return audit_repo.getAllAuditsForPageAuditRecord(audit_id);
	}
	
	public Optional<DomainAuditRecord> findMostRecentDomainAuditRecord(long id) {
		return audit_record_repo.findMostRecentDomainAuditRecord(id);
	}
	
	public Optional<PageAuditRecord> findMostRecentPageAuditRecord(String page_url) {
		assert page_url != null;
		assert !page_url.isEmpty();
		
		return audit_record_repo.getMostRecentPageAuditRecord(page_url);
	}
	
	public Set<Audit> findMostRecentAuditsForPage(String page_url) {
		assert page_url != null;
		assert !page_url.isEmpty();
		
		//get most recent page state
		PageState page_state = page_state_service.findByUrl(page_url);
		return audit_repo.getMostRecentAuditsForPage(page_state.getKey());
		//return audit_record_repo.findMostRecentDomainAuditRecord(page_url);
	}

	public Set<Audit> getAllColorPaletteAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageColorPaletteAudits(audit_record_key);
	}

	public Set<Audit> getAllTextColorContrastAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageTextColorContrastAudits(audit_record_key);
	}

	public Set<Audit> getAllNonTextColorContrastAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageNonTextColorContrastAudits(audit_record_key);
	}

	public Set<Audit> getAllTypefaceAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageTypefaceAudits(audit_record_key);
	}

	
	public Set<Audit> getAllLinkAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageLinkAudits(audit_record_key);
	}

	public Set<Audit> getAllTitleAndHeaderAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageTitleAndHeaderAudits(audit_record_key);
	}

	public Set<Audit> getAllAltTextAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageAltTextAudits(audit_record_key);
	}


	public Set<Audit> getAllMarginAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageMarginAudits(audit_record_key);
	}

	public Set<Audit> getAllPagePaddingAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPagePaddingAudits(audit_record_key);
	}

	public Set<Audit> getAllPageParagraphingAudits(String audit_record_key) {
		assert audit_record_key != null;
		assert !audit_record_key.isEmpty();
		
		return audit_repo.getAllPageParagraphingAudits(audit_record_key);
	}

	public Set<PageAuditRecord> getAllPageAudits(long audit_record_id) {		
		return audit_record_repo.getAllPageAudits(audit_record_id);
	}
	
	public Set<Audit> getAllAuditsForPageAuditRecord(long page_audit_id) {		
		return audit_repo.getAllAuditsForPageAuditRecord( page_audit_id);
	}

	public void addPageAuditToDomainAudit(long domain_audit_record_id, String page_audit_record_key) {
		//check if audit already exists for page state
		audit_record_repo.addPageAuditRecord(domain_audit_record_id, page_audit_record_key);
	}


	public void addPageAuditToDomainAudit(long domain_audit_id, long page_audit_id) {
		audit_record_repo.addPageAuditRecord(domain_audit_id, page_audit_id);
	}
	
	public Optional<PageAuditRecord> getMostRecentPageAuditRecord(String url) {
		assert url != null;
		assert !url.isEmpty();
		
		return audit_record_repo.getMostRecentPageAuditRecord(url);
	}

	@Deprecated
	public PageState getPageStateForAuditRecord(String page_audit_key) {
		assert page_audit_key != null;
		assert !page_audit_key.isEmpty();
		
		return audit_record_repo.getPageStateForAuditRecord(page_audit_key);
	}

	public Set<Audit> getAllContentAuditsForDomainRecord(long id) {
		return audit_repo.getAllContentAuditsForDomainRecord(id);
	}

	public Set<Audit> getAllInformationArchitectureAuditsForDomainRecord(long id) {
		return audit_repo.getAllInformationArchitectureAuditsForDomainRecord(id);
	}

	public Set<Audit> getAllAccessibilityAuditsForDomainRecord(long id) {
		return audit_repo.getAllAccessibilityAuditsForDomainRecord(id);
	}

	public Set<Audit> getAllAestheticAuditsForDomainRecord(long id) {
		return audit_repo.getAllAestheticsAuditsForDomainRecord(id);
	}

	public Set<Audit> getAllContentAudits(long audit_record_id) {
		return audit_repo.getAllContentAudits(audit_record_id);
	}

	public Set<Audit> getAllInformationArchitectureAudits(long id) {
		return audit_repo.getAllInformationArchitectureAudits(id);
	}

	public Set<Audit> getAllAccessibilityAudits(Long id) {
		return audit_repo.getAllAccessibilityAudits(id);
	}

	public Set<Audit> getAllAestheticAudits(long id) {
		return audit_repo.getAllAestheticsAudits(id);
	}

	public Set<UXIssueMessage> getIssues(long audit_record_id) {
		return audit_record_repo.getIssues(audit_record_id);
	}

	public Set<PageState> getPageStatesForDomainAuditRecord(long audit_record_id) {
		return audit_record_repo.getPageStatesForDomainAuditRecord(audit_record_id);
	}

	public void addPageToAuditRecord(long audit_record_id, long page_state_id) {
		audit_record_repo.addPageToAuditRecord( audit_record_id, page_state_id );		
	}

	public long getIssueCountBySeverity(long id, String severity) {
		return audit_record_repo.getIssueCountBySeverity(id, severity);
	}

	public int getPageAuditCount(long domain_audit_id) {
		return audit_record_repo.getPageAuditRecordCount(domain_audit_id);
	}

	public Set<Audit> getAllAudits(long id) {
		return audit_repo.getAllAudits(id);
	}

	public boolean isDomainAuditComplete(AuditRecord audit_record) {		
		//audit_record should now have a domain audit record
		//get all page audit records for domain audit

		Set<PageAuditRecord> page_audits = audit_record_repo.getAllPageAudits(audit_record.getId());
		if(audit_record.getDataExtractionProgress() < 1.0) {
			return false;
		}
		//check all page audit records. If all are complete then the domain is also complete
		for(PageAuditRecord audit : page_audits) {
			if(!audit.isComplete()) {
				return false;
			}
		}
		
		return true;
	}

	public Optional<DomainAuditRecord> getDomainAuditRecordForPageRecord(long id) {
		return audit_record_repo.getDomainForPageAuditRecord(id);
	}

	public Set<Label> getLabelsForImageElements(long id) {
		return audit_record_repo.getLabelsForImageElements(id);
	}

	public Optional<DesignSystem> getDesignSystem(long audit_record_id) {
		return audit_record_repo.getDesignSystem(audit_record_id);
	}
	
	public AuditRecord addJourney(long audit_record_id, long journey_id) {
		return audit_record_repo.addJourney(audit_record_id, journey_id);
	}

	/**
	 * Update the progress for the appropriate {@linkplain AuditCategory}
	 * @param auditRecordId
	 * @param category
	 * @param account_id
	 * @param domain_id
	 * @param progress
	 * @param message
	 * @return
	 */
	public AuditRecord updateAuditProgress(long auditRecordId, 
										   AuditCategory category, 
										   long account_id, 
										   long domain_id, 
										   double progress, 
										   String message) 
	{
		AuditRecord audit_record = findById(auditRecordId).get();
		audit_record.setDataExtractionProgress(1.0);
		audit_record.setStatus(ExecutionStatus.RUNNING_AUDITS);

		if(AuditCategory.CONTENT.equals(category)) {
			audit_record.setContentAuditProgress( progress );
			audit_record.setContentAuditMsg( message);
		}
		else if(AuditCategory.AESTHETICS.equals(category)) {
			audit_record.setAestheticAuditProgress( progress);
			audit_record.setAestheticMsg(message);
		}
		else if(AuditCategory.INFORMATION_ARCHITECTURE.equals(category)) {
			audit_record.setInfoArchitectureAuditProgress( progress );
			audit_record.setInfoArchMsg(message);
		}
		
		return save(audit_record, account_id, domain_id);
	}

	/**
	 * Retrieves {@link PageState} with given URL for {@link DomainAuditRecord}  
	 * @param audit_record_id
	 * @param current_url
	 * @return
	 */
	public PageState findPageWithUrl(long audit_record_id, String url) {
		return audit_record_repo.findPageWithUrl(audit_record_id, url);
	}
	
	/**
	 * Retrieves {@link PageState} with given URL for {@link DomainAuditRecord}  
	 * @param audit_record_id
	 * @param current_url
	 * @return
	 */
	public AuditRecord findPageWithId(long audit_record_id, long page_id) {
		return audit_record_repo.findPageWithId(audit_record_id, page_id);
	}

	public boolean wasPageAlreadyAudited(long domainAuditRecordId, long pageId) {
		return audit_record_repo.wasPageAlreadyAudited(domainAuditRecordId, pageId) != null;
	}
}
