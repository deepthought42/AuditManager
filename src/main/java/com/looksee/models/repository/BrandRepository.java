package com.looksee.models.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.competitiveanalysis.brand.Brand;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * 
 */
@Repository
@Retry(name = "neoforj")
public interface BrandRepository extends Neo4jRepository<Brand, Long> {
	public Brand findByKey(@Param("key") String key);

}
