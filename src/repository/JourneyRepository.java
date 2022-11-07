package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import com.looksee.models.journeys.Journey;
import com.looksee.models.journeys.Step;

public interface JourneyRepository extends Neo4jRepository<Journey, Long>  {
	
	public Journey findByKey(@Param("key") String key);

	@Query("MATCH (j:Journey) WITH j MATCH (s:Step) WHERE id(s)=$step_id AND id(j)=$journey_id MERGE (j)-[:HAS]->(s) RETURN s")
	public Step addStep(@Param("journey_id") long journey_id, @Param("step_id") long id);
}
