package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.Screenshot;

/**
 * 
 */
public interface ScreenshotRepository extends Neo4jRepository<Screenshot, Long> {
	@Query("MATCH (p:Screenshot{key:$key}) RETURN p LIMIT 1")
	public Screenshot findByKey(@Param("key") String key);
}
