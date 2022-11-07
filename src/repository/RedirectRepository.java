package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.journeys.Redirect;

@Repository
public interface RedirectRepository extends Neo4jRepository<Redirect, Long> {
	public Redirect findByKey(@Param("key") String key);
}
