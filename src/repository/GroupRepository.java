package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.Group;

/**
 * 
 */
public interface GroupRepository extends Neo4jRepository<Group, Long> {
	public Group findByKey(@Param("key") String key);
}
