package com.looksee.auditManager.models.journeys;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.looksee.auditManager.models.LookseeObject;


/**
 * Represents the series of steps taken for an end to end journey
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Node
public class Journey extends LookseeObject {

	@Relationship(type = "HAS")
	private List<Step> steps;
	
	private List<Long> orderedIds;
	
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
		return "journey"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(StringUtils.join(orderedIds, "|"));
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
		return orderedIds;
	}
	
	public void setOrderedIds(List<Long> ordered_ids) {
		this.orderedIds = ordered_ids;
	}
}
