package com.looksee.models.audit.performance;

/**
 * Defines detail item for "total-byte-weight" and  in the Google PageSpeed API 
 */
public class AssetSize extends AuditDetail {

	private String url;
	private Integer total_bytes;
	
	public AssetSize() {}
	
	public AssetSize(String url, Integer total_bytes) {
		setUrl(url);
		setTotalBytes(total_bytes);
	}

	/** GETTERS AND SETTERS */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getTotalBytes() {
		return total_bytes;
	}

	public void setTotalBytes(Integer total_bytes) {
		this.total_bytes = total_bytes;
	}

}
