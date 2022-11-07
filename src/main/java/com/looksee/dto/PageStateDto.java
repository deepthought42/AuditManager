package com.looksee.dto;

import com.looksee.models.PageState;

/**
 * Data transfer object representing a {@link PageState} in a format readable for browser extensions
 */
public class PageStateDto {

	private String key;
	private String url;
	private String type;
	
	public PageStateDto(PageState page){
		setKey(page.getKey());
		setUrl(page.getUrl());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
