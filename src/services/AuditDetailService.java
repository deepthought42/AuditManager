package com.looksee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.audit.performance.AuditDetail;
import com.looksee.models.repository.AuditDetailRepository;

/**
 * 
 */
@Service
public class AuditDetailService {
	
	@Autowired
	private AuditDetailRepository audit_detail_repo;
	
	/**
	 * Objects are expected to be immutable as of 3/14/19. When this method is ran, if the 
	 * object already exists then it will be loaded from the database by key, otherwise it will be saved
	 * 
	 * @param audit {@link Audit} 
	 * @return
	 */
	public AuditDetail save(AuditDetail audit_detail){
		return audit_detail_repo.save( audit_detail );
	}
}
