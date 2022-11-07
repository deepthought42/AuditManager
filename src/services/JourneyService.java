package com.looksee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.journeys.Journey;
import com.looksee.models.journeys.Step;
import com.looksee.models.repository.JourneyRepository;

@Service
public class JourneyService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JourneyService.class.getName());

	@Autowired
	private JourneyRepository journey_repo;
	
	public Journey save(Journey journey) {
		Journey journey_record = journey_repo.findByKey(journey.getKey());
		if(journey_record != null) {
			return journey_record;
		}
		journey_record = new Journey();

		journey_record.setOrderedIds(journey.getOrderedIds());
		journey_record.setKey(journey.generateKey());
		journey_record = journey_repo.save(journey_record);
		for(Step step : journey.getSteps()) {
			journey_repo.addStep(journey_record.getId(), step.getId());
		}
		
		journey_record.setSteps(journey.getSteps());
		return journey_record;	
	}
	
}
