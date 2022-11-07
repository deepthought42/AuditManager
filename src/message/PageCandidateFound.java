package com.looksee.models.message;

import java.net.URL;

public class PageCandidateFound extends Message {

	private URL url;

	public PageCandidateFound(long account_id, 
							  long audit_record_id,
							  long domain_id, 
							  URL url
	) {
		super(domain_id, account_id, audit_record_id);
		setUrl(url);
	}
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}	
}
