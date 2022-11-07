package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.looksee.models.AuditSubcategoryStat;
import com.looksee.models.audit.AuditStats;

public interface AuditSubcategoryStatRepository extends Neo4jRepository<AuditSubcategoryStat, Long> {
	
}
