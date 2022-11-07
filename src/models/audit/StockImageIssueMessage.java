package com.looksee.models.audit;

import java.util.Set;

import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.Priority;

/**
 * A observation of potential error for a given {@link Element element} 
 */
public class StockImageIssueMessage extends ElementStateIssueMessage {	
	private boolean stock_image;

	public StockImageIssueMessage() {}
	
	public StockImageIssueMessage(
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
			boolean is_stock_image
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
		
		setStockImage(is_stock_image);
	}

	public boolean isStockImage() {
		return stock_image;
	}

	public void setStockImage(boolean stock_image) {
		this.stock_image = stock_image;
	}
}
