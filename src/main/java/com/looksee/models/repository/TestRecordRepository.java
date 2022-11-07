package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.TestRecord;

@Repository
public interface TestRecordRepository extends Neo4jRepository<TestRecord, Long> {
	
	@Query("MATCH b=(tr:TestRecord{key:$key})-[:HAS_RESULT]->(p) MATCH a=(p)-[]->() RETURN a,b")
	public TestRecord findByKey(@Param("key") String key);
	
	@Query("MATCH (tr:TestRecord{key:$key}) SET tr.status={status} RETURN tr")
	public TestRecord updateStatus(@Param("key") String key, @Param("status") String status);
}
