package com.looksee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.audit.AuditStats;
import com.looksee.models.repository.AuditStatsRepository;

import io.github.resilience4j.retry.annotation.Retry;

@Service
@Retry(name = "neoforj")
public class AuditStatService {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuditStatsRepository audit_stat_repository;
	
	public AuditStats save(AuditStats audit_stat) {
		return audit_stat_repository.save(audit_stat);
	}
}
