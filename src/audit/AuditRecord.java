package com.looksee.models.audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.looksee.models.LookseeObject;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.ExecutionStatus;

/**
 * Record detailing an set of {@link Audit audits}.
 */
public class AuditRecord extends LookseeObject {
	private String url;
	
	private String status;
	private String status_message;
	private String level;
	private LocalDateTime start_time;
	private LocalDateTime end_time;
	private double content_audit_progress;
	private String content_audit_msg;
	
	private double info_arch_audit_progress;
	private String info_arch_msg;
	
	private double aesthetic_audit_progress;
	private String aesthetic_msg;
	
	private double data_extraction_progress;
	private String data_extraction_msg;

	private String target_user_age;
	private String target_user_education;

	//DESIGN SYSTEM VALUES
	private List<String> colors;
	
	public AuditRecord() {
		setStartTime(LocalDateTime.now());
		setStatus(ExecutionStatus.UNKNOWN);
		setUrl("");
		setStatusMessage("");
		setLevel(AuditLevel.UNKNOWN);
		setContentAuditProgress(0.0);
		setContentAuditMsg("");
		setInfoArchitectureAuditProgress(0.0);
		setInfoArchMsg("");
		setAestheticAuditProgress(0.0);
		setAestheticMsg("");
		setDataExtractionProgress(0.0);
		setDataExtractionMsg("");
		setColors(new ArrayList<>());
	}
	
	/**
	 * Constructor
	 * @param level TODO
	 * 
	 */
	public AuditRecord(long id, 
					   ExecutionStatus status, 
					   AuditLevel level, 
					   String key, 
					   LocalDateTime startTime,
					   double aestheticAuditProgress, 
					   String aestheticMsg, 
					   String contentAuditMsg, 
					   double contentAuditProgress,
					   String infoArchMsg, 
					   double infoArchAuditProgress, 
					   String dataExtractionMsg, 
					   double dataExtractionProgress,
					   LocalDateTime created_at, 
					   LocalDateTime endTime, 
					   String url
	) {
		setId(id);
		setStatus(status);
		setLevel(level);
		setKey(key);
		setStartTime(endTime);
		setAestheticAuditProgress(dataExtractionProgress);
		setAestheticMsg(aestheticMsg);
		setContentAuditMsg(contentAuditMsg);
		setContentAuditProgress(contentAuditProgress);
		setInfoArchMsg(infoArchMsg);
		setInfoArchitectureAuditProgress(infoArchAuditProgress);
		setDataExtractionMsg(dataExtractionMsg);
		setDataExtractionProgress(dataExtractionProgress);
		setCreatedAt(created_at);
		setEndTime(endTime);
		setColors(new ArrayList<>());
		setUrl(url);
	}

	public String generateKey() {
		return "auditrecord:" + UUID.randomUUID().toString() + org.apache.commons.codec.digest.DigestUtils.sha256Hex(System.currentTimeMillis() + "");
	}

	public ExecutionStatus getStatus() {
		return ExecutionStatus.create(status);
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status.getShortName();
	}

	public AuditLevel getLevel() {
		return AuditLevel.create(level);
	}

	public void setLevel(AuditLevel level) {
		this.level = level.toString();
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
	
	public double getContentAuditProgress() {
		return content_audit_progress;
	}

	public void setContentAuditProgress(double content_audit_progress) {
		this.content_audit_progress = content_audit_progress;
	}

	public double getInfoArchitechtureAuditProgress() {
		return info_arch_audit_progress;
	}

	public void setInfoArchitectureAuditProgress(double info_arch_audit_progress) {
		this.info_arch_audit_progress = info_arch_audit_progress;
	}

	public double getAestheticAuditProgress() {
		return aesthetic_audit_progress;
	}

	public void setAestheticAuditProgress(double aesthetic_audit_progress) {
		this.aesthetic_audit_progress = aesthetic_audit_progress;
	}

	public String getContentAuditMsg() {
		return content_audit_msg;
	}

	public void setContentAuditMsg(String content_audit_msg) {
		this.content_audit_msg = content_audit_msg;
	}
	
	public String getInfoArchMsg() {
		return info_arch_msg;
	}

	public void setInfoArchMsg(String info_arch_msg) {
		this.info_arch_msg = info_arch_msg;
	}

	public String getAestheticMsg() {
		return aesthetic_msg;
	}

	public void setAestheticMsg(String aesthetic_msg) {
		this.aesthetic_msg = aesthetic_msg;
	}

	public double getDataExtractionProgress() {
		return data_extraction_progress;
	}

	public void setDataExtractionProgress(double data_extraction_progress) {
		this.data_extraction_progress = data_extraction_progress;
	}

	public String getDataExtractionMsg() {
		return data_extraction_msg;
	}

	public void setDataExtractionMsg(String data_extraction_msg) {
		this.data_extraction_msg = data_extraction_msg;
	}

	public String getTargetUserAge() {
		return target_user_age;
	}

	public void setTargetUserAge(String target_user_age) {
		this.target_user_age = target_user_age;
	}

	public String getTargetUserEducation() {
		return target_user_education;
	}

	public void setTargetUserEducation(String target_user_education) {
		this.target_user_education = target_user_education;
	}

	public String getStatusMessage() {
		return status_message;
	}

	public void setStatusMessage(String status_message) {
		this.status_message = status_message;
	}
	
	public boolean isComplete() {
		return (this.getAestheticAuditProgress() >= 1.0
				&& this.getContentAuditProgress() >= 1.0
				&& this.getInfoArchitechtureAuditProgress() >= 1.0
				&& this.getDataExtractionProgress() >= 1.0);
	}
	
	@Override
	public AuditRecord clone() {
		return new AuditRecord(getId(),
							   getStatus(),
							   getLevel(),
							   getKey(),
							   getStartTime(),
							   getAestheticAuditProgress(), 
							   getAestheticMsg(), 
							   getContentAuditMsg(), 
							   getContentAuditProgress(), 
							   getInfoArchMsg(), 
							   getInfoArchitechtureAuditProgress(),
							   getDataExtractionMsg(), 
							   getDataExtractionProgress(), 
							   getCreatedAt(), 
							   getEndTime(),
							   getUrl());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}
	
	public boolean addColor(String color){
		if(!getColors().contains(color)) {
			return getColors().add(color);
		}
		
		return true;	
	}
}
