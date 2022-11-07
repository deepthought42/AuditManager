package com.looksee.models.audit.performance;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.annotation.Properties;

/**
 * 
 */
public class ThirdPartySummaryDetail extends AuditDetail {

	private Integer transfer_size;
	private Double blocking_time;
	private Double main_thread_time;
	
	@Properties
	private Map<String, String> entity = new HashMap<>();
	
	public ThirdPartySummaryDetail() {}
	
	public ThirdPartySummaryDetail(int transfer_size, double blocking_time, double main_thread_time, Map<String, String> entity) {
		setTransferSize(transfer_size);
		setBlockingTime(blocking_time);
		setMainThreadTime(main_thread_time);
		setEntity(entity);
	}

	public Integer getTransferSize() {
		return transfer_size;
	}
	
	public void setTransferSize(Integer transfer_size) {
		this.transfer_size = transfer_size;
	}
	
	public Double getBlockingTime() {
		return blocking_time;
	}
	
	public void setBlockingTime(Double blocking_time) {
		this.blocking_time = blocking_time;
	}
	
	public Double getMainThreadTime() {
		return main_thread_time;
	}
	
	public void setMainThreadTime(Double main_thread_time) {
		this.main_thread_time = main_thread_time;
	}
	
	public Map<String, String> getEntity() {
		return entity;
	}
	
	public void setEntity(Map<String, String> entity) {
		this.entity = entity;
	}
}
