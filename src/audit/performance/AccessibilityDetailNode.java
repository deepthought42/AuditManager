package com.looksee.models.audit.performance;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.ElementState;

/**
 * Defines an accessibility audit detail record. 
 */
public class AccessibilityDetailNode extends AuditDetail {	
	
	///THE FOLLOWING ARE THE NEW FIELDS
	private String[] required_change_messages;
	private String[] optional_change_messages;
	
	@Relationship(type = "BELONGS_TO")
	private ElementState element;

	//END OF NEW FIELDS


	public AccessibilityDetailNode() {}
	
	/**
	 * 
	 * @param node_label
	 * @param explanation
	 * @param type
	 * @param selector
	 * @param path
	 * @param snippet
	 */
	public AccessibilityDetailNode(
			String[] required_messages,
			String[] optional_messages,
			ElementState element
	) {
		setRequiredChangeMessages(required_messages);
		setOptionalChangeMessages(optional_messages);
		setElement(element);
	}

	/** GETTERS AND SETTERS */

	public ElementState getElement() {
		return element;
	}

	public void setElement(ElementState element) {
		this.element = element;
	}

	public String[] getRequiredChangeMessages() {
		return required_change_messages;
	}

	public void setRequiredChangeMessages(String[] required_change_messages) {
		this.required_change_messages = required_change_messages;
	}

	public String[] getOptionalChangeMessages() {
		return optional_change_messages;
	}

	public void setOptionalChangeMessages(String[] optional_change_messages) {
		this.optional_change_messages = optional_change_messages;
	}
	
}
