package com.looksee.dto;

import com.looksee.models.ActionOLD;

/**
 * Data Transfer Object for {@link ActionOLD} 
 */
public class ActionDto {

	private String key;
	private String value;
	private String name;
	
	public ActionDto(ActionOLD action){
		setKey(action.getKey());
		setValue(action.getValue());
		setName(action.getName());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
