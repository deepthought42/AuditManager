package com.looksee.models.journeys;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.enums.Action;

/**
 * A Step is the increment of work that start with a {@link PageState} contians an {@link ElementState} 
 * 	 that has an {@link Action} performed on it and results in an end {@link PageState}
 */
@NodeEntity
public class SimpleStep extends Step {
	
	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private ElementState element;
	
	private String action;
	private String action_input;
	
	public SimpleStep() {
		super();
		setActionInput("");
		setAction(Action.UNKNOWN);
	}
	
	public SimpleStep(PageState start_page,
				ElementState element,
				Action action,
				String action_input, 
				PageState end_page) {
		setStartPage(start_page);
		setElementState(element);
		setAction(action);
		setActionInput(action_input);
		setEndPage(end_page);
		setKey(generateKey());
	}
	
	@Override
	public SimpleStep clone() {
		return new SimpleStep(getStartPage(), getElementState(), getAction(), getActionInput(), getEndPage());
	}
	
	public ElementState getElementState() {
		return this.element;
	}
	
	public void setElementState(ElementState element) {
		this.element = element;
	}
	
	public Action getAction() {
		return Action.create(action);
	}
	
	public void setAction(Action action) {
		this.action = action.getShortName();
	}

	@Override
	public String generateKey() {
		String key = "";
		if(getStartPage() != null) {
			key += getStartPage().getId();
		}
		if(element != null) {
			key += element.getId();
		}
		if(getEndPage() != null) {
			key += getEndPage().getId();
		}
		return "simplestep"+key+action+action_input;
	}

	
	@Override
	public String toString() {
		return "key = "+getKey()+",\n start_page = "+getStartPage()+"\n element ="+getElementState()+"\n end page = "+getEndPage();
	}
	
	public String getActionInput() {
		return action_input;
	}

	public void setActionInput(String action_input) {
		this.action_input = action_input;
	}
}
