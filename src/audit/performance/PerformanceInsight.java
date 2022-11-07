package com.looksee.models.audit.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.Persistable;
import com.looksee.models.enums.CaptchaResult;
import com.looksee.models.enums.FormFactor;

/**
 * Stores page speed analytics data that is returned as part of requests to Google Page Insights
 * 
 */
@NodeEntity
public class PerformanceInsight implements Persistable {

	@GeneratedValue
    @Id
	private Long id;
	
	private String key;
	private Date executed_at;
	private Double timing;
	private String request_id;
	private String locale;
	private String captcha_result;
	private List<Object> run_warnings;
	private String runtime_error_code;
	private String runtime_error_message;
	private String emulated_form_factor;
	private List<String> categories;
	private Double speed_score;
	private Double accessibility_score;
	private Double seo_score;
	private Double overall_score;
	
	@Relationship(type = "HAS")
	private List<PageSpeedAudit> audits = new ArrayList<>();
	
	public PerformanceInsight() {}
	
	public PerformanceInsight(
			Date executed_at,
			Double timing,
			String request_id,
			String locale,
			CaptchaResult captcha_result,
			List<Object> run_warnings,
			FormFactor emulated_form_factor
	) {
		setExecutedAt(executed_at);
		setTiming(timing);
		setRequestId(request_id);
		setLocale(locale);
		setCaptchaResult(captcha_result);
		setRunWarnings(run_warnings);
		setEmulatedFormFactor(emulated_form_factor);
		setSpeedScore(0.0);
		setKey(generateKey());
	}
	
	/* GETTERS AND SETTERS */
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getExecutedAt() {
		return executed_at;
	}
	
	public void setExecutedAt(Date executed_at) {
		this.executed_at = executed_at;
	}
	
	public Double getTiming() {
		return timing;
	}
	
	public void setTiming(Double timing) {
		this.timing = timing;
	}
	
	public String getRequestId() {
		return request_id;
	}
	
	public void setRequestId(String request_id) {
		this.request_id = request_id;
	}
	
	public CaptchaResult getCaptchaResult() {
		return CaptchaResult.create( captcha_result );
	}
	
	public void setCaptchaResult(CaptchaResult captchaResult) {
		this.captcha_result = captchaResult.toString();
	}
	
	public List<Object> getRunWarnings() {
		return run_warnings;
	}
	
	public void setRunWarnings(List<Object> run_warnings) {
		this.run_warnings = run_warnings;
	}
	
	public FormFactor getEmulatedFormFactor() {
		return FormFactor.create( emulated_form_factor );
	}
	
	public void setEmulatedFormFactor(FormFactor emulated_form_factor) {
		this.emulated_form_factor = emulated_form_factor.toString();
	}
	
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public List<String> getCategories() {
		return categories;
	}
	
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getRuntimeErrorCode() {
		return runtime_error_code;
	}

	public void setRuntimeErrorCode(String runtime_error_code) {
		this.runtime_error_code = runtime_error_code;
	}

	public String getRuntimeErrorMessage() {
		return runtime_error_message;
	}

	public void setRuntimeErrorMessage(String runtime_error_message) {
		this.runtime_error_message = runtime_error_message;
	}

	public List<PageSpeedAudit> getAudits() {
		return audits;
	}

	public void setAudits(List<PageSpeedAudit> audits) {
		this.audits = audits;
	}

	public void addAudit(PageSpeedAudit audit) {
		this.audits.add(audit);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String generateKey() {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.getRequestId() + this.getExecutedAt());
	}

	public Double getSpeedScore() {
		return speed_score;
	}

	public void setSpeedScore(Double score) {
		this.speed_score = score;
	}

	public Double getAccessibilityScore() {
		return accessibility_score;
	}

	public void setAccessibilityScore(Double accessibility_score) {
		this.accessibility_score = accessibility_score;
	}

	public Double getSeoScore() {
		return seo_score;
	}

	public void setSeoScore(Double seo_score) {
		this.seo_score = seo_score;
	}

	public Double getOverallScore() {
		return overall_score;
	}
	
	public void setOverallScore(double page_score) {
		this.overall_score = page_score;
	}

}
