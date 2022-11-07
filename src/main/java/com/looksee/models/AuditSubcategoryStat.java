package com.looksee.models;

import java.time.LocalDateTime;

import com.looksee.models.enums.AuditName;

public class AuditSubcategoryStat extends LookseeObject{
	
	private LocalDateTime start_time; //time that audit subcategory is started
	private LocalDateTime end_time; //time that audit of subcategory is completed
	private String subcategory;
	private long pages_completed;
	private String url;
	
	public AuditSubcategoryStat() {}
	
	public AuditSubcategoryStat(String url) {
		setUrl(url);
		setStartTime(LocalDateTime.now());
	}
	
	public AuditSubcategoryStat(AuditName subcategory, 
								LocalDateTime start_time, 
								LocalDateTime end_time, 
								int page_count,
								String host) {
		setStartTime(start_time);
		setEndTime(end_time);
		setPagesCompleted(page_count);
		setSubcategory(subcategory);
	}

	public LocalDateTime getStartTime() {
		return start_time;
	}
	
	public void setStartTime(LocalDateTime start_time) {
		this.start_time = start_time;
	}
	
	public LocalDateTime getEndTime() {
		return end_time;
	}
	
	public void setEndTime(LocalDateTime end_time) {
		this.end_time = end_time;
	}
	
	public long getPagesCompleted() {
		return pages_completed;
	}
	
	public void setPagesCompleted(long pages_completed) {
		this.pages_completed = pages_completed;
	}
	
	public void setSubcategory(AuditName subcategory) {
		this.subcategory = subcategory.toString();
	}
	
	public AuditName getSubcategory() {
		return AuditName.valueOf(subcategory);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String generateKey() {
		return "auditsubcategorystat"+org.apache.commons.codec.digest.DigestUtils.sha512Hex( start_time + url);
	}
}
