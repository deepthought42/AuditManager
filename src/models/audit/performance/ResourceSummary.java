package com.looksee.models.audit.performance;

/**
 * Contains {@link AuditDetail}s for the "resource-summary" performance audit in googe pagespeed api
 * 
 */
public class ResourceSummary extends AuditDetail {

	private Integer resource_count;
	private String resource_type;
	private String label;
	private Long size;
	
	public ResourceSummary() {}
	
	public ResourceSummary(Integer resource_count, String resource_type, String label, Long size) {
		setResourceCount(resource_count);
		setResourceType(resource_type);
		setLabel(label);
		setSize(size);
	}

	public Integer getResourceCount() {
		return resource_count;
	}

	public void setResourceCount(Integer resource_count) {
		this.resource_count = resource_count;
	}

	public String getResourceType() {
		return resource_type;
	}

	public void setResourceType(String resource_type) {
		this.resource_type = resource_type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

}
