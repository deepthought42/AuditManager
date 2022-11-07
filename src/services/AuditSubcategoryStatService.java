package com.looksee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.AuditSubcategoryStat;
import com.looksee.models.repository.AuditSubcategoryStatRepository;

@Service
public class AuditSubcategoryStatService {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuditSubcategoryStatRepository audit_stat_repository;
	
	public AuditSubcategoryStat save(AuditSubcategoryStat audit_stat) {
		return audit_stat_repository.save(audit_stat);
	}
}
