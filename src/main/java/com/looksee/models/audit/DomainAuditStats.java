package com.looksee.models.audit;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.AuditSubcategoryStat;
import com.looksee.models.Label;
import com.looksee.models.enums.ExecutionStatus;

public class DomainAuditStats extends AuditStats{	
	
	private long total_pages_audited;
	private long high_impact_issue_count;
	private long mid_impact_issue_count;
	private long low_impact_issue_count;
	
	//crawler tracking
	private long pages_examined;
	private long pages_found;
	
	@Relationship(type = "HAS")
	private Set<AuditSubcategoryStat> subcategory_stats;
	
	private Set<Label> image_labels;
	
	public DomainAuditStats() {}
	
	public DomainAuditStats(long audit_record_id) {
		setStartTime(LocalDateTime.now());
		setAuditRecordId(audit_record_id);
	}
	
	public DomainAuditStats(
			long audit_record_id,
			LocalDateTime start_time,
			LocalDateTime end_time,
			long pages_examined,
			long page_count,
			long content_pages_audited,
			double content_audit_progress,
			int written_content_issue_count,
			int imagery_issue_count,
			int video_issue_count,
			int audio_issue_count,
			double written_content_score,
			double imagery_score,
			double videos_score,
			double audio_score,
			String content_msg,
			long info_arch_pages_audited,
			double info_arch_audit_progress,
			int seo_issue_count,
			int menu_issue_count,
			int performance_issue_count,
			int link_issue_count,
			double seo_score,
			double menu_analysis_score,
			double performance_score,
			double link_score,
			String info_arch_msg, 
			long aesthetic_pages_audited, 
			double aesthetic_audit_progress,
			int text_contrast_issue_count,
			int non_text_issue_count,
			int typography_issue_count,
			int whitespace_issue_count,
			int branding_issue_count,
			double text_contrast_score,
			double non_text_contrast_score,
			double typography_score, 
			double whitespace_score,
			double branding_score,
			String aesthetic_msg,
			double overall_score, 
			long high_impact_issues,
			long mid_impact_issues, 
			long low_impact_issues,
			long elements_examined,
			long elements_found, 
			String data_extraction_msg, 
			double data_extraction_progress, 
			List<SimpleScore> overall_score_history, 
			List<SimpleScore> content_score_history, 
			List<SimpleScore> info_architecture_score_history, 
			List<SimpleScore> aesthetic_score_history, 
			List<SimpleScore> accessibility_score_history, 
			int total_issues, 
			Set<Label> image_labels,
			int image_copyright_issue_count, 
			ExecutionStatus execution_status
	) {
		setStatus(execution_status);
		setStartTime(start_time);
		setEndTime(end_time);
		setAuditRecordId(audit_record_id);
		setSubcategoryStats(new HashSet<>());
		setContentPagesAudited(content_pages_audited);
		setContentAuditProgress(content_audit_progress);
		
		setWrittenContentIssueCount(written_content_issue_count);
		setImageryIssueCount(imagery_issue_count);
		setVideoIssueCount(video_issue_count);
		setAuditIssueCount(audio_issue_count);
		setWrittenContentScore(written_content_score);
		setImageryScore(imagery_score);
		setVideosScore(videos_score);
		setAudioScore(audio_score);

		setContentMsg(content_msg);
		setInfoArchPagesAudited(info_arch_pages_audited);
		setInfoArchAuditProgress(info_arch_audit_progress);
		setSeoIssueCount(seo_issue_count);
		setMenuIssueCount(menu_issue_count);
		setLinkIssueCount(link_issue_count);
		setPerformanceIssueCount(performance_issue_count);
		setSeoScore(seo_score);
		setMenuAnalysisScore(menu_analysis_score);
		setLinkScore(link_score);
		setPerformanceScore(performance_score);
		setInfoArchMsg(info_arch_msg);
		
		setAestheticPagesAudited(aesthetic_pages_audited);
		setAestheticAuditProgress(aesthetic_audit_progress);
		setTextContrastIssueCount(text_contrast_issue_count);
		setNonTextContrastIssueCount(non_text_issue_count);
		setTypographyIssueCount(typography_issue_count);
		setWhitespaceIssueCount(whitespace_issue_count);
		setBrandingIssueCount(branding_issue_count);
		
		setTextContrastScore(text_contrast_score);
		setNonTextContrastScore(non_text_contrast_score);
		setTypographyScore(typography_score);
		setWhitespaceScore(whitespace_score);
		setBrandingScore(branding_score);
		setAestheticMsg(aesthetic_msg);
		
		setTotalIssues(total_issues);
		setOverallScore(overall_score);
		setHighImpactIssueCount(high_impact_issues);
		setMidImpactIssueCount(mid_impact_issues);
		setLowImpactIssueCount(low_impact_issues);
		
		setDataExtractionProgress(data_extraction_progress);
		setDataExtractionMessage(data_extraction_msg);
		
		setTotalPagesAudited(page_count);
		setPagesFound(page_count);
		setPagesExamined(pages_examined);
		
		setOverallScoreHistory(overall_score_history);
		setContentScoreHistory(content_score_history);
		setInfoArchitectureScoreHistory(info_architecture_score_history);
		setAestheticScoreHistory(aesthetic_score_history);
		setAccessibilityScoreHistory(accessibility_score_history);
		
		setImageLabels(image_labels);
		setImageCopyrightIssueCount(image_copyright_issue_count);
	}


	public long getTotalPagesAudited() {
		return total_pages_audited;
	}

	public void setTotalPagesAudited(long total_pages_audited) {
		this.total_pages_audited = total_pages_audited;
	}

	public long getHighImpactIssueCount() {
		return high_impact_issue_count;
	}

	public void setHighImpactIssueCount(long high_impact_issue_cnt) {
		this.high_impact_issue_count = high_impact_issue_cnt;
	}

	public long getMidImpactIssueCount() {
		return mid_impact_issue_count;
	}

	public void setMidImpactIssueCount(long mid_impact_issue_count) {
		this.mid_impact_issue_count = mid_impact_issue_count;
	}

	public long getLowImpactIssueCount() {
		return low_impact_issue_count;
	}

	public void setLowImpactIssueCount(long low_impact_issue_count) {
		this.low_impact_issue_count = low_impact_issue_count;
	}

	public long getPagesExamined() {
		return pages_examined;
	}

	public void setPagesExamined(long pages_examined) {
		this.pages_examined = pages_examined;
	}

	public long getPagesFound() {
		return pages_found;
	}

	public void setPagesFound(long pages_found) {
		this.pages_found = pages_found;
	}

	public Set<Label> getImageLabels() {
		return image_labels;
	}

	public void setImageLabels(Set<Label> img_labels) {
		this.image_labels = img_labels;
	}
}
