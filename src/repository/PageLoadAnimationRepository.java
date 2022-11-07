package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.PageLoadAnimation;

public interface PageLoadAnimationRepository extends Neo4jRepository<PageLoadAnimation, Long> {
	public PageLoadAnimation findByKey(@Param("key") String key);
}
