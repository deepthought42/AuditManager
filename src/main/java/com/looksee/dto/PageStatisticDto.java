package com.looksee.dto;

/**
 * A simplified data set for page consisting of full page and viewport screenshots, url and the height and width
 *  of the full page screenshot
 *
 */
public class PageStatisticDto {
	private long id;
	private String url;
	private long audit_record_id;
	private String screenshot_url;
	private double content_score;
	private double content_progress;
	private double info_arch_score;
	private double info_arch_progress;
	private double accessibility_score;
	private double accessibility_progress;
	private double aesthetic_score;
	private double aesthetic_progress;
	private double data_extraction_progress;
	private long elements_extracted;
	private long elements_reviewed;
	private boolean is_complete;
	
	public PageStatisticDto(
			long id, 
			String url, 
			String screenshot_url, 
			double content_score, 
			double content_progress, 
			double info_arch_score, 
			double info_arch_progress, 
			double accessibility_score, 
			double accessibility_progress, 
			double aesthetic_score, 
			double aesthetic_progress, 
			long audit_record_id, 
			long elements_reviewed, 
			long elements_extracted, 
			boolean is_complete, 
			double data_extraction_progress
	) {
		setId(id);
		setUrl(url);
		setAuditRecordId(audit_record_id);
		setScreenshotUrl(screenshot_url);
		setContentScore(content_score);
		setContentProgress(content_progress);
		setInfoArchScore(info_arch_score);
		setInfoArchProgress(info_arch_progress);
		setAccessibilityScore(accessibility_score);
		setAccessibilityProgress(accessibility_progress);
		setAestheticScore(aesthetic_score);
		setAestheticProgress(aesthetic_progress);
		setElementsReviewed(elements_reviewed);
		setElementsExtracted(elements_extracted);
		setDataExtractionProgress(data_extraction_progress);
		setComplete(is_complete);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getScreenshotUrl() {
		return screenshot_url;
	}

	public void setScreenshotUrl(String screenshot_url) {
		this.screenshot_url = screenshot_url;
	}

	public double getContentScore() {
		return content_score;
	}

	public void setContentScore(double content_score) {
		this.content_score = content_score;
	}

	public double getInfoArchScore() {
		return info_arch_score;
	}

	public void setInfoArchScore(double info_arch_score) {
		this.info_arch_score = info_arch_score;
	}

	public double getAccessibilityScore() {
		return accessibility_score;
	}

	public void setAccessibilityScore(double accessibility_score) {
		this.accessibility_score = accessibility_score;
	}

	public double getAestheticScore() {
		return aesthetic_score;
	}

	public void setAestheticScore(double aesthetic_score) {
		this.aesthetic_score = aesthetic_score;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAuditRecordId() {
		return audit_record_id;
	}

	public void setAuditRecordId(long audit_record_id) {
		this.audit_record_id = audit_record_id;
	}

	public long getElementsExtracted() {
		return elements_extracted;
	}

	public void setElementsExtracted(long elements_extracted) {
		this.elements_extracted = elements_extracted;
	}

	public long getElementsReviewed() {
		return elements_reviewed;
	}

	public void setElementsReviewed(long elements_reviewed) {
		this.elements_reviewed = elements_reviewed;
	}

	public double getContentProgress() {
		return content_progress;
	}

	public void setContentProgress(double content_progress) {
		this.content_progress = content_progress;
	}

	public double getInfoArchProgress() {
		return info_arch_progress;
	}

	public void setInfoArchProgress(double info_arch_progress) {
		this.info_arch_progress = info_arch_progress;
	}

	public double getAccessibilityProgress() {
		return accessibility_progress;
	}

	public void setAccessibilityProgress(double accessibility_progress) {
		this.accessibility_progress = accessibility_progress;
	}

	public double getAestheticProgress() {
		return aesthetic_progress;
	}

	public void setAestheticProgress(double aesthetic_progress) {
		this.aesthetic_progress = aesthetic_progress;
	}

	public boolean isComplete() {
		return is_complete;
	}

	public void setComplete(boolean is_complete) {
		this.is_complete = is_complete;
	}

	public double getDataExtractionProgress() {
		return data_extraction_progress;
	}

	public void setDataExtractionProgress(double data_extraction_progress) {
		this.data_extraction_progress = data_extraction_progress;
	}

}
