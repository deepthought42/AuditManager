package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.ElementState;
import com.looksee.models.ImageSearchAnnotation;

import io.github.resilience4j.retry.annotation.Retry;

@Repository
@Retry(name = "neoforj")
public interface ImageSearchAnnotationRepository extends Neo4jRepository<ImageSearchAnnotation, Long> {
	
	@Query("MATCH (e:ImageSearchAnnotation{key:$key}) RETURN e LIMIT 1")
	public ImageSearchAnnotation findByKey(@Param("key") String key);
}
