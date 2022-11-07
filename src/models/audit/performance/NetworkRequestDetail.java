package com.looksee.models.audit.performance;


/**
 * 
 */
public class NetworkRequestDetail extends AuditDetail {

	private Integer transfer_size;
	private String url;
	private Integer status_code;
	private String resource_type;
	private String mime_type;
	private Integer resource_size;
	private Double end_time;
	private Double start_time;
	
	public NetworkRequestDetail() {}
	
	public NetworkRequestDetail(Integer transfer_size, String url, Integer status_code, String resource_type, 
			String mime_type,Integer resource_size, Double end_time, Double start_time) {
		setTransferSize(transfer_size);
		setUrl(url);
		setStatusCode(status_code);
		setResourceType(resource_type);
		setMimeType(mime_type);
		setResourceSize(resource_size);
		setEndTime(end_time);
		setStartTime(start_time);
	}
	
	/** GETTERS AND SETTERS */
	public Integer getTransferSize() {
		return transfer_size;
	}
	
	public void setTransferSize(Integer transfer_size) {
		this.transfer_size = transfer_size;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getStatusCode() {
		return status_code;
	}
	
	public void setStatusCode(Integer status_code) {
		this.status_code = status_code;
	}
	
	public String getResourceType() {
		return resource_type;
	}
	
	public void setResourceType(String resource_type) {
		this.resource_type = resource_type;
	}
	
	public String getMimeType() {
		return mime_type;
	}
	
	public void setMimeType(String mime_type) {
		this.mime_type = mime_type;
	}
	
	public Integer getResourceSize() {
		return resource_size;
	}
	
	public void setResourceSize(Integer resource_size) {
		this.resource_size = resource_size;
	}
	
	public Double getEndTime() {
		return end_time;
	}
	
	public void setEndTime(Double end_time) {
		this.end_time = end_time;
	}
	
	public Double getStartTime() {
		return start_time;
	}
	
	public void setStartTime(Double start_time) {
		this.start_time = start_time;
	}
}
