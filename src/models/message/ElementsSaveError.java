package com.looksee.models.message;

public class ElementsSaveError extends Message {
	private long page_id;
	private String page_url;
	
	public ElementsSaveError(long account_id, 
							 long page_id, 
							 long audit_record_id, 
							 long domain_id, 
							 String page_url
	) {
		super(domain_id, account_id, audit_record_id);
		assert page_url != null;
		assert !page_url.isEmpty();
		
		setPageId(page_id);
		setPageUrl(page_url);
	}

	public long getPageId() {
		return page_id;
	}

	public void setPageId(long page_id) {
		this.page_id = page_id;
	}

	public String getPageUrl() {
		return page_url;
	}

	public void setPageUrl(String page_url) {
		this.page_url = page_url;
	}

}
