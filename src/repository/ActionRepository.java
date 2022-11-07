package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.ActionOLD;

/**
 * 
 */
@Repository
public interface ActionRepository extends Neo4jRepository<ActionOLD, Long> {
	public	ActionOLD findByKey(@Param("key") String key);

}
