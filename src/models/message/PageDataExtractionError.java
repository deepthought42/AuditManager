package com.looksee.models.message;

public class PageDataExtractionError extends Message {
	private String url;
	private String error_message;
	
	public PageDataExtractionError(long domainId, 
								   long accountId, 
								   long auditRecordId, 
								   String url, 
								   String error_message) {
		super(domainId, accountId, auditRecordId);
		setUrl(url);
		setErrorMessage(error_message);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getErrorMessage() {
		return error_message;
	}

	public void setErrorMessage(String error_message) {
		this.error_message = error_message;
	}

}
