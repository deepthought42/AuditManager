package com.looksee.models.audit.performance;

/**
 * 
 * 
 */
public class TimingDetail extends AuditDetail {

	private String name;
	private Double start_time;
	private Double duration;
	private String timing_type;
	
	public TimingDetail() {}
	
	public TimingDetail(String name, Double start_time, Double duration, String timing_type) {
		setName(name);
		setStartTime(start_time);
		setDuration(duration);
		setTimingType(timing_type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getStartTime() {
		return start_time;
	}

	public void setStartTime(Double start_time) {
		this.start_time = start_time;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getTimingType() {
		return timing_type;
	}

	public void setTimingType(String timing_type) {
		this.timing_type = timing_type;
	}
	
	
}
