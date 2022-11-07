package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.ImageFaceAnnotation;

import io.github.resilience4j.retry.annotation.Retry;

@Repository
@Retry(name = "neoforj")
public interface ImageFaceAnnotationRepository extends Neo4jRepository<ImageFaceAnnotation, Long> {
	
	@Query("MATCH (e:ImageFaceAnnotation{key:$key}) RETURN e LIMIT 1")
	public ImageFaceAnnotation findByKey(@Param("key") String key);
}
