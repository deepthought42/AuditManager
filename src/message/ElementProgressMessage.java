package com.looksee.models.message;

import java.util.List;

import com.looksee.models.ElementState;

/**
 * Message for different audit actions to perform and which audit types to perform them for.
 * 
 */
public class ElementProgressMessage extends Message{
	private long page_state_id;
	private String page_url;
	private List<String> xpaths_explored;
	private List<ElementState> element_states;
	private long total_xpaths;
	private long total_dispatches;

	
	public ElementProgressMessage(
			long account_id, 
			long audit_record_id, 
			long page_state_id, 
			List<String> xpaths, 
			List<ElementState> element_states, 
			long total_xpaths, 
			long total_dispatches, 
			String page_url,
			long domain_id
	){
		super(domain_id, account_id, audit_record_id);
		setXpathsExplored(xpaths);
		setPageStateId(page_state_id);
		setElementStates(element_states);
		setTotalXpaths(total_xpaths);
		setTotalDispatches(total_dispatches);
		setPageUrl(page_url);
	}

	public long getPageStateId() {
		return page_state_id;
	}

	public void setPageStateId(long page_state_id) {
		this.page_state_id = page_state_id;
	}

	public List<String> getXpathsExplored() {
		return xpaths_explored;
	}

	public void setXpathsExplored(List<String> xpaths_explored) {
		this.xpaths_explored = xpaths_explored;
	}

	public List<ElementState> getElementStates() {
		return element_states;
	}

	public void setElementStates(List<ElementState> element_states) {
		this.element_states = element_states;
	}

	public long getTotalXpaths() {
		return total_xpaths;
	}

	public void setTotalXpaths(long total_xpaths) {
		this.total_xpaths = total_xpaths;
	}

	public long getTotalDispatches() {
		return total_dispatches;
	}

	public void setTotalDispatches(long total_dispatches) {
		this.total_dispatches = total_dispatches;
	}

	public String getPageUrl() {
		return page_url;
	}

	public void setPageUrl(String page_url) {
		this.page_url = page_url;
	}	
}
