package com.looksee.models.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.audit.performance.PageSpeedAudit;
import com.looksee.models.audit.performance.PerformanceInsight;

/**
 * 
 * 
 */
@Repository
public interface PerformanceInsightRepository  extends Neo4jRepository<PerformanceInsight, Long> {
	public PerformanceInsight findByKey(@Param("key") String key);

	@Query("MATCH(:Account{user_id:$user_id})-[]-(d:Domain{url:$domain_url}) MATCH (d)-[:HAS]->(p:Page) MATCH (p)-[:HAS]->(i:PerformanceInsight{key:$performance_insight_key}),(audit:Audit{key:$audit_key}) MERGE (i)-[:HAS]->(audit) RETURN audit LIMIT 1")
	public PageSpeedAudit addAudit(@Param("user_id") String user_id, @Param("domain_url") String domain_url, @Param("performance_insight_key") String performance_insight_key, @Param("audit_key") String audit_key);

	@Query("MATCH(:Account{user_id:$user_id})-[]-(d:Domain{url:$domain_url}) MATCH (d)-[:HAS]->(p:Page) MATCH (p)-[]->(i:PerformanceInsight{key:$performance_insight_key}) MATCH (i)-[:HAS]->(audit:Audit{key:$audit_key}) RETURN audit LIMIT 1")
	public PageSpeedAudit findAuditByKey(@Param("user_id") String user_id, @Param("domain_url") String domain_url, @Param("performance_insight_key") String performance_insight_key, @Param("audit_key") String audit_key);

	@Query("MATCH (p:Page{key:$page_key})-[:HAS]->(insight:PerformanceInsight{key:$insight_key}) MATCH (insight)-[:HAS]->(audit) OPTIONAL MATCH z=(audit)-->(x) OPTIONAL MATCH y=(x)-->(detail) RETURN z,y")
	public List<PageSpeedAudit> getAllAudits(@Param("page_key") String page_key, @Param("insight_key") String insight_key);

}
