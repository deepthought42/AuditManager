package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.looksee.models.TestUser;

/**
 * 
 */
public interface TestUserRepository extends Neo4jRepository<TestUser, Long> {

}
