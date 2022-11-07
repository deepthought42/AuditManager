package com.looksee.dto;

import com.looksee.models.ActionOLD;
import com.looksee.models.Element;

/**
 * Data Transfer object that describes an object composed of both {@link Element} and {@link ActionOLD}
 * 
 */
public class ElementActionDto{

	private ElementStateDto element;
	private ActionDto action;
	
	public ElementActionDto(Element elem, ActionOLD action){
		setElement(new ElementStateDto(elem));
		setAction(new ActionDto(action));
	}

	public ElementStateDto getElement() {
		return element;
	}

	private void setElement(ElementStateDto element_dto) {
		this.element = element_dto;
	}

	public ActionDto getAction() {
		return action;
	}

	private void setAction(ActionDto action_dto) {
		this.action = action_dto;
	}
}
