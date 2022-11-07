package com.looksee.models.journeys;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.LookseeObject;

/**
 * Represents the series of steps taken for an end to end journey
 */
public class Journey extends LookseeObject {

	@Relationship(type = "HAS")
	private List<Step> steps;
	
	private List<Long> ordered_ids;
	
	public Journey() {
		setSteps(new ArrayList<>());
		setOrderedIds(new ArrayList<>());
		setKey(generateKey());
	}
	
	public Journey(List<Step> steps) {
		List<Long> ordered_ids = steps.stream()
									  .map(step -> step.getId())
									  .filter(id -> id != null)
									  .collect(Collectors.toList());
		setSteps(steps);
		setOrderedIds(ordered_ids);
		setKey(generateKey());
	}
	
	public Journey(List<Step> steps, List<Long> ordered_keys) {
		setSteps(steps);
		setOrderedIds(ordered_keys);
		setKey(generateKey());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateKey() {
		return "journey"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(StringUtils.join(ordered_ids, "|"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Journey clone() {
		return new Journey(new ArrayList<>(getSteps()), new ArrayList<>(getOrderedIds()));
	}
	
	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public boolean addStep(SimpleStep step) {
		return this.steps.add(step);
	}
	
	public List<Long> getOrderedIds() {
		return ordered_ids;
	}
	
	public void setOrderedIds(List<Long> ordered_ids) {
		this.ordered_ids = ordered_ids;
	}
}
