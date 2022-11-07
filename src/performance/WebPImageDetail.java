package com.looksee.models.audit.performance;


/**
 * 
 */
public class WebPImageDetail extends AuditDetail {

	private int wasted_bytes;
	private String url;
	private boolean from_protocol;
	private boolean is_cross_origin;
	private int total_bytes;
	
	public WebPImageDetail() {}
	
	public WebPImageDetail(int wasted_bytes, String url, boolean from_protocol, boolean is_cross_origin, int total_bytes) {
		setWastedBytes(wasted_bytes);
		setUrl(url);
		setFromProtocol(from_protocol);
		setIsCrossOrigin(is_cross_origin);
		setTotalBytes(total_bytes);
	}
	
	public int getWastedBytes() {
		return wasted_bytes;
	}
	public void setWastedBytes(int wasted_bytes) {
		this.wasted_bytes = wasted_bytes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isFromProtocol() {
		return from_protocol;
	}
	public void setFromProtocol(boolean from_protocol) {
		this.from_protocol = from_protocol;
	}
	public boolean isIsCrossOrigin() {
		return is_cross_origin;
	}
	public void setIsCrossOrigin(boolean is_cross_origin) {
		this.is_cross_origin = is_cross_origin;
	}
	public int getTotalBytes() {
		return total_bytes;
	}
	public void setTotalBytes(int total_bytes) {
		this.total_bytes = total_bytes;
	}

}
