package com.looksee.services;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Service;

import com.looksee.models.competitiveanalysis.Competitor;
import com.looksee.models.competitiveanalysis.brand.Brand;
import com.looksee.models.repository.CompetitorRepository;

/**
 * Contains business logic for interacting with and managing accounts
 *
 */
@Service
public class CompetitorService {

	@Autowired
	private CompetitorRepository competitor_repo;
	

	public Competitor save(Competitor competitor) {
		return competitor_repo.save(competitor);
	}

	public void delete(Competitor competitor) {
		competitor_repo.delete(competitor);
	}

	public void deleteById(long competitor_id) {
		competitor_repo.deleteById(competitor_id);
	}

	public Iterable<Competitor> getAll() {
		return competitor_repo.findAll();
	}

	public void addBrand(long competitor_id, long brand_id) {
		competitor_repo.addBrand(competitor_id, brand_id);
	}

	public Optional<Competitor> findById(long competitor_id) {
		return competitor_repo.findById(competitor_id);
	}

	/**
	 * Checks if competitor has a competitive analysis currently in progress
	 * 
	 * @param id
	 * @return
	 */
	public boolean isAnalysisRunning(Brand brand) {
		if(brand != null) {
			Instant created = brand.getCreatedAt().toInstant(ZoneOffset.UTC);
			Instant now = Instant.now();
			Instant fifteen_minutes_ago = Instant.now().minusSeconds( (60*15) );
			return ( ! created.isBefore( fifteen_minutes_ago ) )  // "Not before" means "Is equal to or later".
				    			&& created.isBefore( now );
		}
	
		return false;
	}

	public Brand getMostRecentBrand(long competitor_id) {
		return competitor_repo.getMostRecentBrand(competitor_id);
	}
}
