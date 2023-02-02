package com.looksee.auditManager.models.journeys;


import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.looksee.auditManager.models.LookseeObject;
import com.looksee.auditManager.models.PageState;
import com.looksee.auditManager.models.enums.StepType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleStep.class, name = "SIMPLE"),
    @JsonSubTypes.Type(value = LoginStep.class, name = "LOGIN"),
    @JsonSubTypes.Type(value = LandingStep.class, name = "LANDING")  
})
public abstract class Step extends LookseeObject{
	
	abstract StepType getStepType();


	@Relationship(type = "STARTS_WITH")
	private PageState startPage;
	
	@Relationship(type = "ENDS_WITH")
	private PageState endPage;
	
	public PageState getStartPage() {
		return startPage;
	}

	public void setStartPage(PageState page_state) {
		this.startPage = page_state;
	}

	public PageState getEndPage() {
		return endPage;
	}

	public void setEndPage(PageState page_state) {
		this.endPage = page_state;
	}
	
	/**
	 * Perform deep clone of object
	 */
	public abstract Step clone();
}
