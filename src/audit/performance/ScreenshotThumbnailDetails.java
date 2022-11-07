package com.looksee.models.audit.performance;


/**
 * 
 */
public class ScreenshotThumbnailDetails extends AuditDetail {

	private Double timing;
	private Long timestamp;
	private String data;
	
	public ScreenshotThumbnailDetails() {}
	
	public ScreenshotThumbnailDetails(Double timing, Long timestamp, String data) {
		setTiming(timing);
		setTimestamp(timestamp);
		setData(data);
	}

	public Double getTiming() {
		return timing;
	}

	public void setTiming(Double timing) {
		this.timing = timing;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
