package com.looksee.models;

public class RenderedPageState {

	private PageState page_state;
	public RenderedPageState(PageState page) {
		this.page_state = page;
	}
	
	public PageState getPageState() {
		return page_state;
	}
}
