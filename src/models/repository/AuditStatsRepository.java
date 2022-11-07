package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.looksee.models.audit.AuditStats;

public interface AuditStatsRepository extends Neo4jRepository<AuditStats, Long> {
	
}
