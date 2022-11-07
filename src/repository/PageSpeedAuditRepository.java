package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.audit.performance.PageSpeedAudit;

/**
 * Repository interface for Spring Data Neo4j to handle interactions with {@link Audit} objects
 */
public interface PageSpeedAuditRepository extends Neo4jRepository<PageSpeedAudit, Long> {
	public PageSpeedAudit findByKey(@Param("key") String key);
}
