package com.looksee.models.audit;

import java.util.Set;

import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.Priority;

/**
 * A observation of potential error for a given {@link Element element} 
 */
public class SentenceIssueMessage extends ElementStateIssueMessage {	
	
	private int word_count;
	
	public SentenceIssueMessage() {}
	
	public SentenceIssueMessage(
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
			int word_count
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
		
		setWordCount(word_count);
	}

	public int getWordCount() {
		return word_count;
	}

	public void setWordCount(int word_count) {
		this.word_count = word_count;
	}

}
