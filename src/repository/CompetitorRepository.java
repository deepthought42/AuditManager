package com.looksee.models.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.looksee.models.competitiveanalysis.Competitor;
import com.looksee.models.competitiveanalysis.brand.Brand;

@Repository
public interface CompetitorRepository extends Neo4jRepository<Competitor, Long> {

	@Query("MATCH (c:Competitor), (brand:Brand) WHERE id(c)=$competitor_id AND id(brand)=$brand_id MERGE (c)-[r:USES]->(brand) return brand")
	public void addBrand(@Param("competitor_id") long competitor_id, @Param("brand_id") long brand_id);

	@Query("MATCH (c:Competitor)-[r:USES]->(brand:Brand) WHERE id(c)=$competitor_id RETURN brand ORDER BY brand.created_at DESC LIMIT 1")
	public Brand getMostRecentBrand(@Param("competitor_id") long competitor_id);
}
