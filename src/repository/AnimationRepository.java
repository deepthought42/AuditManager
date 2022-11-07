package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.Animation;

public interface AnimationRepository extends Neo4jRepository<Animation, Long> {
	public Animation findByKey(@Param("key") String key);
}
