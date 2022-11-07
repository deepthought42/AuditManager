package com.looksee.models.audit;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.AuditSubcategoryStat;
import com.looksee.models.enums.ExecutionStatus;

public class AuditStats {
	
	private LocalDateTime start_time; //time that the 
	private LocalDateTime end_time;
	private long audit_record_id;
	private String status;
	
	//12 month historical scores
	private List<SimpleScore> overall_score_history;
	private List<SimpleScore> content_score_history;
	private List<SimpleScore> info_architecture_score_history;
	private List<SimpleScore> aesthetic_score_history;
	private List<SimpleScore> accessibility_score_history;
	
	//crawl progress trackers
	private long elements_examined;
	private long elements_found;
	private double data_extraction_progress;
	private String data_extraction_message;
	
	private long content_pages_audited;
	private double content_score;
	private double content_audit_progress;
	private String content_msg;
	
	//content sub-category score
	private int written_content_issue_count;
	private int imagery_issue_count;
	private int video_issue_count;
	private int audit_issue_count;
	private int image_copyright_issue_count;

	private double written_content_score;
	private double imagery_score;
	private double videos_score;
	private double audio_score;
	
	private long info_arch_pages_audited;
	private double info_arch_score;
	private double info_arch_audit_progress;
	private String info_arch_msg;
	
	//info architecture audit sub-categories
	private int seo_issue_count;
	private int menu_issue_count;
	private int performance_issue_count;
	private int link_issue_count;
	
	private double seo_score;
	private double menu_analysis_score;
	private double performance_score;
	private double link_score;
	
	private long aesthetic_pages_audited;
	private double aesthetic_score;
	private double aesthetic_audit_progress;
	private String aesthetic_msg;
	
	//aesthetic audit sub-categories
	private int text_contrast_issue_count;
	private int non_text_issue_count;
	private int typography_issue_count;
	private int whitespace_issue_count;
	private int branding_issue_count;
	
	private double text_contrast_score;
	private double non_text_contrast_score;
	private double typography_score;
	private double whitespace_score;
	private double branding_score;
		
	private int total_issues;
	private double overall_score;

	@Relationship(type = "HAS")
	private Set<AuditSubcategoryStat> subcategory_stats;
	
	public AuditStats() {}
	
	public AuditStats(long audit_record_id) {
		setStartTime(LocalDateTime.now());
		setAuditRecordId(audit_record_id);
	}
	
	public AuditStats(
			long audit_record_id,
			LocalDateTime start_time, 
			LocalDateTime end_time,
			long content_pages_audited,
			double content_audit_progress,
			double content_score,
			String content_msg,
			int written_content_issue_count,
			int imagery_issue_count,
			int video_issue_count,
			int audit_issue_count,
			double written_content_score,
			double imagery_score,
			double videos_score,
			double audio_score, 
			long info_arch_pages_audited, 
			double info_arch_audit_progress, 
			double info_arch_score, 
			String info_arch_msg,
			int seo_issue_count,
			int menu_issue_count,
			int performance_issue_count,
			double seo_score,
			double menu_analysis_score,
			double performance_score, 
			long aesthetic_pages_audited, 
			double aesthetic_audit_progress, 
			double aesthetic_score, 
			String aesthetic_msg,
			int text_contrast_issue_count,
			int non_text_issue_count,
			int typography_issue_count,
			int whitespace_issue_count,
			int branding_issue_count,
			double color_score, 
			double typography_score, 
			double whitespace_score, 
			double branding_score,
			int total_issues,
			long elements_examined, 
			long elements_found, 
			double data_extraction_progress,
			String data_extraction_msg,
			List<SimpleScore> overall_score_history, 
			List<SimpleScore> content_score_history, 
			List<SimpleScore> info_architecture_score_history, 
			List<SimpleScore> aesthetic_score_history, 
			List<SimpleScore> accessibility_score_history, 
			int image_copyright_issue_count
	) {
		setStartTime(start_time);
		setEndTime(end_time);
		setAuditRecordId(audit_record_id);
		setSubcategoryStats(new HashSet<>());
		setContentPagesAudited(content_pages_audited);
		setContentAuditProgress(content_audit_progress);
		setContentScore(content_score);
		setContentMsg(content_msg);
		setWrittenContentScore(written_content_score);
		setImageryScore(imagery_score);
		setVideosScore(videos_score);
		setAudioScore(audio_score);
		setInfoArchPagesAudited(info_arch_pages_audited);
		setInfoArchAuditProgress(info_arch_audit_progress);
		setInfoArchMsg(info_arch_msg);
		setSeoScore(seo_score);
		setMenuAnalysisScore(menu_analysis_score);
		setPerformanceScore(performance_score);
		setTextContrastScore(text_contrast_score);
		setNonTextContrastScore(non_text_contrast_score);
		setTypographyScore(typography_score);
		setWhitespaceScore(whitespace_score);
		setBrandingScore(branding_score);
		setAestheticPagesAudited(aesthetic_pages_audited);
		setAestheticAuditProgress(aesthetic_audit_progress);
		setAestheticMsg(aesthetic_msg);
		setElementsExamined(elements_examined);
		setElementsFound(elements_found);
		setDataExtractionProgress(data_extraction_progress);
		setDataExtractionMessage(data_extraction_msg);
		setTotalIssues(total_issues);
		
		setOverallScoreHistory(overall_score_history);
		setContentScoreHistory(content_score_history);
		setInfoArchitectureScoreHistory(info_architecture_score_history);
		setAestheticScoreHistory(aesthetic_score_history);
		setAccessibilityScoreHistory(accessibility_score_history);
	}


	public double getOverallScore() {
		return overall_score;
	}

	public void setOverallScore(double overall_score) {
		this.overall_score = overall_score;
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

	public Set<AuditSubcategoryStat> getSubcategoryStats() {
		return subcategory_stats;
	}

	public void setSubcategoryStats(Set<AuditSubcategoryStat> subcategory_stats) {
		this.subcategory_stats = subcategory_stats;
	}

	public long getContentPagesAudited() {
		return content_pages_audited;
	}

	public void setContentPagesAudited(long content_pages_audited) {
		this.content_pages_audited = content_pages_audited;
	}

	public long getInfoArchPagesAudited() {
		return info_arch_pages_audited;
	}

	public void setInfoArchPagesAudited(long info_arch_pages_audited) {
		this.info_arch_pages_audited = info_arch_pages_audited;
	}

	public long getAestheticPagesAudited() {
		return aesthetic_pages_audited;
	}

	public void setAestheticPagesAudited(long aesthetic_pages_audited) {
		this.aesthetic_pages_audited = aesthetic_pages_audited;
	}

	public long getAuditRecordId() {
		return audit_record_id;
	}

	public void setAuditRecordId(long audit_record_id) {
		this.audit_record_id = audit_record_id;
	}

	public double getContentAuditProgress() {
		return content_audit_progress;
	}

	public void setContentAuditProgress(double content_audit_progress) {
		this.content_audit_progress = content_audit_progress;
	}

	public double getInfoArchAuditProgress() {
		return info_arch_audit_progress;
	}

	public void setInfoArchAuditProgress(double info_arch_audit_progress) {
		this.info_arch_audit_progress = info_arch_audit_progress;
	}

	public double getAestheticAuditProgress() {
		return aesthetic_audit_progress;
	}

	public void setAestheticAuditProgress(double aesthetic_audit_progress) {
		this.aesthetic_audit_progress = aesthetic_audit_progress;
	}

	public String getContentMsg() {
		return content_msg;
	}

	public void setContentMsg(String content_msg) {
		this.content_msg = content_msg;
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

	public double getAestheticScore() {
		return aesthetic_score;
	}

	public void setAestheticScore(double aesthetic_pages_score) {
		this.aesthetic_score = aesthetic_pages_score;
	}

	public double getInfoArchScore() {
		return info_arch_score;
	}

	public void setInfoArchScore(double info_arch_pages_score) {
		this.info_arch_score = info_arch_pages_score;
	}

	public double getContentScore() {
		return content_score;
	}

	public void setContentScore(double content_pages_score) {
		this.content_score = content_pages_score;
	}

	public double getWrittenContentScore() {
		return written_content_score;
	}

	public void setWrittenContentScore(double written_content_score) {
		this.written_content_score = written_content_score;
	}

	public double getImageryScore() {
		return imagery_score;
	}

	public void setImageryScore(double imagery_score) {
		this.imagery_score = imagery_score;
	}

	public double getAudioScore() {
		return audio_score;
	}

	public void setAudioScore(double audio_score) {
		this.audio_score = audio_score;
	}

	public double getVideosScore() {
		return videos_score;
	}

	public void setVideosScore(double videos_score) {
		this.videos_score = videos_score;
	}

	public double getSeoScore() {
		return seo_score;
	}

	public void setSeoScore(double seo_score) {
		this.seo_score = seo_score;
	}

	public double getMenuAnalysisScore() {
		return menu_analysis_score;
	}

	public void setMenuAnalysisScore(double menu_analysis_score) {
		this.menu_analysis_score = menu_analysis_score;
	}

	public double getPerformanceScore() {
		return performance_score;
	}

	public void setPerformanceScore(double performance_score) {
		this.performance_score = performance_score;
	}

	public double getTypographyScore() {
		return typography_score;
	}

	public void setTypographyScore(double typography_score) {
		this.typography_score = typography_score;
	}

	public double getWhitespaceScore() {
		return whitespace_score;
	}

	public void setWhitespaceScore(double whitespace_score) {
		this.whitespace_score = whitespace_score;
	}

	public double getBrandingScore() {
		return branding_score;
	}

	public void setBrandingScore(double branding_score) {
		this.branding_score = branding_score;
	}

	public long getElementsExamined() {
		return elements_examined;
	}

	public void setElementsExamined(long elements_examined) {
		this.elements_examined = elements_examined;
	}

	public long getElementsFound() {
		return elements_found;
	}

	public void setElementsFound(long elements_found) {
		this.elements_found = elements_found;
	}

	public double getDataExtractionProgress() {
		return data_extraction_progress;
	}

	public void setDataExtractionProgress(double data_extraction_progress) {
		this.data_extraction_progress = data_extraction_progress;
	}

	public String getDataExtractionMessage() {
		return data_extraction_message;
	}

	public void setDataExtractionMessage(String data_extraction_message) {
		this.data_extraction_message = data_extraction_message;
	}
	

	public List<SimpleScore> getOverallScoreHistory() {
		return overall_score_history;
	}

	public void setOverallScoreHistory(List<SimpleScore> overall_scores) {
		this.overall_score_history = overall_scores;
	}

	public List<SimpleScore> getContentScoreHistory() {
		return content_score_history;
	}

	public void setContentScoreHistory(List<SimpleScore> content_scores_history) {
		this.content_score_history = content_scores_history;
	}

	public List<SimpleScore> getInfoArchitectureScoreHistory() {
		return info_architecture_score_history;
	}

	public void setInfoArchitectureScoreHistory(List<SimpleScore> info_architecture_score_history) {
		this.info_architecture_score_history = info_architecture_score_history;
	}

	public List<SimpleScore> getAestheticScoreHistory() {
		return aesthetic_score_history;
	}

	public void setAestheticScoreHistory(List<SimpleScore> aesthetic_score_history) {
		this.aesthetic_score_history = aesthetic_score_history;
	}

	public List<SimpleScore> getAccessibilityScoreHistory() {
		return accessibility_score_history;
	}

	public void setAccessibilityScoreHistory(List<SimpleScore> accessibility_score_history) {
		this.accessibility_score_history = accessibility_score_history;
	}

	public int getWrittenContentIssueCount() {
		return written_content_issue_count;
	}

	public void setWrittenContentIssueCount(int written_content_issue_count) {
		this.written_content_issue_count = written_content_issue_count;
	}

	public int getImageryIssueCount() {
		return imagery_issue_count;
	}

	public void setImageryIssueCount(int imagery_issue_count) {
		this.imagery_issue_count = imagery_issue_count;
	}

	public int getVideoIssueCount() {
		return video_issue_count;
	}

	public void setVideoIssueCount(int video_issue_count) {
		this.video_issue_count = video_issue_count;
	}

	public int getAuditIssueCount() {
		return audit_issue_count;
	}

	public void setAuditIssueCount(int audit_issue_count) {
		this.audit_issue_count = audit_issue_count;
	}

	public int getSeoIssueCount() {
		return seo_issue_count;
	}

	public void setSeoIssueCount(int seo_issue_count) {
		this.seo_issue_count = seo_issue_count;
	}

	public int getMenuIssueCount() {
		return menu_issue_count;
	}

	public void setMenuIssueCount(int menu_issue_count) {
		this.menu_issue_count = menu_issue_count;
	}

	public int getPerformanceIssueCount() {
		return performance_issue_count;
	}

	public void setPerformanceIssueCount(int performance_issue_count) {
		this.performance_issue_count = performance_issue_count;
	}

	public int getTypographyIssueCount() {
		return typography_issue_count;
	}

	public void setTypographyIssueCount(int typography_issue_count) {
		this.typography_issue_count = typography_issue_count;
	}

	public int getWhitespaceIssueCount() {
		return whitespace_issue_count;
	}

	public void setWhitespaceIssueCount(int whitespace_issue_count) {
		this.whitespace_issue_count = whitespace_issue_count;
	}

	public int getBrandingIssueCount() {
		return branding_issue_count;
	}

	public void setBrandingIssueCount(int branding_issue_count) {
		this.branding_issue_count = branding_issue_count;
	}

	public int getTotalIssues() {
		return total_issues;
	}

	public void setTotalIssues(int total_issues) {
		this.total_issues = total_issues;
	}

	public int getLinkIssueCount() {
		return link_issue_count;
	}

	public void setLinkIssueCount(int link_issue_count) {
		this.link_issue_count = link_issue_count;
	}

	public double getLinkScore() {
		return link_score;
	}

	public void setLinkScore(double link_score) {
		this.link_score = link_score;
	}

	public int getTextContrastIssueCount() {
		return text_contrast_issue_count;
	}

	public void setTextContrastIssueCount(int text_contrast_issue_count) {
		this.text_contrast_issue_count = text_contrast_issue_count;
	}

	public int getNonTextContrastIssueCount() {
		return non_text_issue_count;
	}

	public void setNonTextContrastIssueCount(int non_text_issue_count) {
		this.non_text_issue_count = non_text_issue_count;
	}

	public double getTextContrastScore() {
		return text_contrast_score;
	}

	public void setTextContrastScore(double text_contrast_score) {
		this.text_contrast_score = text_contrast_score;
	}

	public double getNonTextContrastScore() {
		return non_text_contrast_score;
	}

	public void setNonTextContrastScore(double non_text_contrast_score) {
		this.non_text_contrast_score = non_text_contrast_score;
	}

	public int getImageCopyrightIssueCount() {
		return image_copyright_issue_count;
	}

	public void setImageCopyrightIssueCount(int image_copyright_issue_count) {
		this.image_copyright_issue_count = image_copyright_issue_count;
	}

	public ExecutionStatus getStatus() {
		return ExecutionStatus.create(status);
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status.toString();
	}
}
