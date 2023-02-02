package com.looksee.auditManager.models;

import java.util.Set;

import com.looksee.auditManager.models.enums.ObservationType;
import com.looksee.auditManager.models.enums.Priority;
import com.looksee.auditManager.models.recommend.Recommendation;


/**
 * Details issues for when a page is devoid of a certain styling such as padding, 
 * that should be used, because it adds extra white-space to the content
 */
public class StylingMissingIssueMessage extends UXIssueMessage {
	
	public StylingMissingIssueMessage(
			String description, 
			Set<Recommendation> recommendation, 
			Priority priority) {
		super();
		
		assert description != null;
		setDescription(description);
		setType(ObservationType.STYLE_MISSING);
		setKey(generateKey());
	}

	@Override
	public ObservationType getType() {
		return ObservationType.STYLE_MISSING;
	}

}
