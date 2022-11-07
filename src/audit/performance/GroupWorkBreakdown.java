package com.looksee.models.audit.performance;

/**
 * Describes the cumulative time used for different resource groups to load.
 */
public class GroupWorkBreakdown extends AuditDetail {

	private String group;
	private String group_label;
	private Double duration;
	
	public GroupWorkBreakdown() {}
	
	public GroupWorkBreakdown(String group, double duration, String group_label ) {
		setGroup(group);
		setGroupLabel(group_label);
		setDuration(duration);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroupLabel() {
		return group_label;
	}

	public void setGroupLabel(String group_label) {
		this.group_label = group_label;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}


}
