package com.looksee.auditManager.models;

import java.util.Set;

import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.Priority;


/**
 * A observation of potential error for a given {@link Element element} 
 */
public class ReadingComplexityIssueMessage extends ElementStateIssueMessage {	
	private double easeOfReadingScore;

	public ReadingComplexityIssueMessage() {}
	
	public ReadingComplexityIssueMessage(
			Priority priority,
			String description,
			String recommendation, 
			ElementState element, 
			AuditCategory category, 
			Set<String> labels, 
			String wcag_compliance,
			String title, 
			int points_awarded,
			int max_points,
			double ease_of_reading_score
	) {
		super(	priority, 
				description, 
				recommendation,
				element,
				category,
				labels,
				wcag_compliance,
				title,
				points_awarded,
				max_points);
		
		setEaseOfReadingScore(ease_of_reading_score);
	}

	public double getEaseOfReadingScore() {
		return easeOfReadingScore;
	}

	public void setEaseOfReadingScore(double ease_of_reading_score) {
		this.easeOfReadingScore = ease_of_reading_score;
	}

}
