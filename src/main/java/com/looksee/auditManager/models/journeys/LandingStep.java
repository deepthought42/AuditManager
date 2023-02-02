package com.looksee.auditManager.models.journeys;


import com.looksee.auditManager.models.enums.Action;
import com.looksee.auditManager.models.enums.StepType;
import com.looksee.auditManager.models.ElementState;
import com.looksee.auditManager.models.PageState;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A Step is the increment of work that start with a {@link PageState} contians an {@link ElementState} 
 * 	 that has an {@link Action} performed on it and results in an end {@link PageState}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("LANDING")
@Node
public class LandingStep extends Step {
	
	@Relationship(type = "STARTS_WITH")
	private PageState startPage;
	
	public LandingStep() {
		super();
	}
	
	public LandingStep(PageState start_page) 
	{
		setStartPage(start_page);
		setKey(generateKey());
	}
	
	public PageState getStartPage() {
		return startPage;
	}

	public void setStartPage(PageState startPage) {
		this.startPage = startPage;
	}

	@Override
	public LandingStep clone() {
		return new LandingStep(getStartPage());
	}
	
	@Override
	public String generateKey() {
		return "landingstep"+getStartPage().getId();
	}

	
	@Override
	public String toString() {
		return "key = "+getKey()+",\n start_page = "+getStartPage();
	}

	@Override
	StepType getStepType() {
		return StepType.LANDING;
	}
}
