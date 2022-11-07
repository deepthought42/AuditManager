package com.looksee.models.audit.performance;

/**
 * 
 */
public class BlockingResource extends AuditDetail {

	private String url;
	private Integer total_bytes;
	private Double wasted_ms;
	
	public BlockingResource() {}
	
	public BlockingResource(String url, Integer total_bytes, Double wasted_ms) {
		setUrl(url);
		setTotalBytes(total_bytes);
		setWastedMs(wasted_ms);
	}

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

	public Double getWastedMs() {
		return wasted_ms;
	}

	public void setWastedMs(Double wasted_ms) {
		this.wasted_ms = wasted_ms;
	}

}
