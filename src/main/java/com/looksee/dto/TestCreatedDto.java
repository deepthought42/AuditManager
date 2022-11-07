package com.looksee.dto;

import com.looksee.models.Test;

/**
 * Data Transfer object for Test information to be relayed to user ide
 */
public class TestCreatedDto {

	private String key;
	private String name;

	public TestCreatedDto(Test test){
		setKey(test.getKey());
		setName(test.getName());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
