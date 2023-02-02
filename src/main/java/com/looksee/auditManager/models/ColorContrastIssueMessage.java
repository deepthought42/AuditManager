package com.looksee.auditManager.models;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.ObservationType;
import com.looksee.auditManager.models.enums.Priority;



/**
 * A observation of potential error for a given color palette 
 */
public class ColorContrastIssueMessage extends ElementStateIssueMessage{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ColorContrastIssueMessage.class);

	private double contrast;
	private String foregroundColor;
	private String backgroundColor;
	private String fontSize;
	
	public ColorContrastIssueMessage() {}
	
	/**
	 * Constructs new instance
	 * 
	 * @param priority
	 * @param description TODO
	 * @param contrast
	 * @param foreground_color
	 * @param background_color
	 * @param element
	 * @param category TODO
	 * @param labels TODO
	 * @param wcag_compliance TODO
	 * @param title TODO
	 * @param font_size TODO
	 * @param points_earned TODO
	 * @param max_points TODO
	 * @param recommendation TODO
	 * @pre priority != null
	 * @pre recommendation != null
	 * @pre !recommendation.isEmpty()
	 * @pre element != null
	 * @pre foreground_color != null
	 * @pre !foreground_color.isEmpty()
	 * @pre assert background_color != null
	 * @pre !background_color.isEmpty()
	 * 
	 */
	public ColorContrastIssueMessage(
			Priority priority, 
			String description,
			double contrast,
			String foreground_color,
			String background_color,
			ElementState element, 
			AuditCategory category, 
			Set<String> labels, 
			String wcag_compliance, 
			String title,
			String font_size, 
			int points_earned, 
			int max_points, 
			String recommendation
	) {
		assert priority != null;
		assert foreground_color != null;
		assert !foreground_color.isEmpty();
		assert background_color != null;
		assert !background_color.isEmpty();

		setPriority(priority);
		setDescription(description);
		setRecommendation(recommendation);
		setContrast(contrast);
		setForegroundColor(foreground_color);
		setBackgroundColor(background_color);
		setElement(element);
		setCategory(category);
		setLabels(labels);
		setType(ObservationType.COLOR_CONTRAST);
		setWcagCompliance(wcag_compliance);
		setTitle(title);
		setFontSize(font_size);
		setPoints(points_earned);
		setMaxPoints(max_points);
		setKey(this.generateKey());
	}

	public double getContrast() {
		return contrast;
	}

	public void setContrast(double contrast) {
		this.contrast = contrast;
	}

	public String getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(String foreground_color) {
		this.foregroundColor = foreground_color;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String background_color) {
		this.backgroundColor = background_color;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String font_size) {
		this.fontSize = font_size;
	}
}
