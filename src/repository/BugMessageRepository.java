package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.audit.performance.BugMessage;

/**
 * Spring Data interface for interacting with {@link BugMessage} objects in database
 */
@Repository
public interface BugMessageRepository extends Neo4jRepository<BugMessage, Long> {

	@Query("MATCH (bm:BugMessage{message:$message}) RETURN bm LIMIT 1")
	public BugMessage findByMessage(@Param("message") String message);
}
