package com.looksee.models.message;

import com.looksee.models.PageState;

public class PageDataExtractionMessage extends Message{
	private String url;
	private int dispatch_count;
	private PageState page_state;
	
	public PageDataExtractionMessage(
			long domain_id,
			long account_id,
			long audit_record_id,
			String url, 
			int dispatch_count, 
			PageState page_state
	) {
		super(domain_id, account_id, audit_record_id);
		setUrl(url);
		setDispatchCount(dispatch_count);
		setPageState(page_state);
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public int getDispatchCount() {
		return dispatch_count;
	}
	public void setDispatchCount(int dispatch_count) {
		this.dispatch_count = dispatch_count;
	}
	public PageState getPageState() {
		return page_state;
	}
	public void setPageState(PageState page_state) {
		this.page_state = page_state;
	}
}
