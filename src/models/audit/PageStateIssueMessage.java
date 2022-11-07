package com.looksee.models.audit;

import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.Element;
import com.looksee.models.PageState;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.ObservationType;
import com.looksee.models.enums.Priority;

/**
 * A observation of potential error for a given {@link Element element} 
 */
public class PageStateIssueMessage extends UXIssueMessage {

	@Relationship(type = "FOR")
	private PageState page_state;
	
	public PageStateIssueMessage() {}
	
	public PageStateIssueMessage(
				PageState page, 
				String description,
				String recommendation, 
				Priority priority, 
				AuditCategory category, 
				Set<String> labels,
				String wcag_compliance, 
				String title, 
				int points_awarded, 
				int max_points
	) {
		super(	priority, 
				description, 
				ObservationType.PAGE_STATE,
				category,
				wcag_compliance,
				labels,
				"",
				title,
				points_awarded,
				max_points,
				recommendation);
		
		setPage(page);
	}

	public PageState getElements() {
		return page_state;
	}


	public void setPage(PageState page_state) {
		this.page_state = page_state;
	}
}
