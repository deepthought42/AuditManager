package com.looksee.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.audit.performance.AuditDetail;
import com.looksee.models.audit.performance.PageSpeedAudit;
import com.looksee.models.repository.AuditDetailRepository;
import com.looksee.models.repository.PageSpeedAuditRepository;

@Service
public class PageSpeedAuditService {

	@Autowired
	private PageSpeedAuditRepository audit_repo;
	
	@Autowired
	private AuditDetailRepository audit_detail_repo;
	
	/**
	 * Objects are expected to be immutable as of 3/14/19. When this method is ran, if the 
	 * object already exists then it will be loaded from the database by key, otherwise it will be saved
	 * 
	 * @param audit {@link PageSpeedAudit} 
	 * @return
	 */
	public PageSpeedAudit save(PageSpeedAudit audit){
		assert audit != null;
		
		/*for(AuditDetail detail : audit.getDetails()) {
		List<AuditDetail> audit_details = new ArrayList<AuditDetail>();
		
		//save audit details
			audit_details.add(audit_detail_repo.save(detail));
		}
		audit.setDetails(audit_details);
		 */
		return audit_repo.save(audit);
	}

	/**
	 * Retrieve data from database
	 * 
	 * @param key
	 * @return
	 */
	public PageSpeedAudit findByKey(String key) {
		return audit_repo.findByKey(key);
	}
}
