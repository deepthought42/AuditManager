package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.rules.Rule;

/**
 * 
 */
public interface RuleRepository extends Neo4jRepository<Rule, Long> {
	public Rule findByKey(@Param("key") String key);

	@Query("MATCH (r:Rule) WHERE r.type=$type AND r.value=$value RETURN r")
	public Rule findByTypeAndValue(@Param("type") String rule_type, 
									@Param("value") String value);
}
