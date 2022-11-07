package com.looksee.models.audit;

import java.util.Set;

import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.Priority;

/**
 * A observation of potential error for a given {@link Element element} 
 */
public class ReadingComplexityIssueMessage extends ElementStateIssueMessage {	
	private double ease_of_reading_score;

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
		return ease_of_reading_score;
	}

	public void setEaseOfReadingScore(double ease_of_reading_score) {
		this.ease_of_reading_score = ease_of_reading_score;
	}

}
