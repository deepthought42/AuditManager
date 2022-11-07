package com.looksee.models.audit.performance;

import java.util.Date;

/**
 * 
 * 
 */
public class MetricsDetail extends AuditDetail {

	private Integer first_contentful_paint;
	private Long observed_first_paint_ts;
	private Integer speed_index;
	private Long observed_speed_index_ts;
	private Integer observed_first_contentful_paint;
	private Long observed_navigation_start_ts;
	private Long observed_largest_contentful_paint_ts;
	private Integer observed_first_visual_change;
	private Long observed_load_ts;
	private Integer first_meaningful_paint;
	private Integer observed_trace_end;
	private Integer observed_first_meaningful_paint;
	private Integer first_cpu_idle;
	private Long observed_trace_end_ts;
	private Long observed_first_meaningful_paint_ts;
	private Integer observed_dom_content_loaded;
	private Long observed_first_visual_change_ts;
	private Integer interactive;
	private Integer observed_navigation_start;
	private Long observed_first_contentful_paint_ts;
	private Long observed_last_visual_change_ts;
	private Integer observed_load;
	private Integer observed_largest_contentful_paint;
	private Long observed_dom_content_loaded_ts;
	private Integer observed_speed_index;
	private Integer estimated_input_latency;
	private Integer total_blocking_time;
	private Integer observed_first_paint;
	private Integer observed_last_visual_change;
	private Boolean lcp_invalidated;
	
	public MetricsDetail() {}
	
	public MetricsDetail(
			Integer first_contentful_paint, 
			Long observed_first_paint_ts, 
			Integer speed_index, 
			Long observed_speed_index_ts,
			Integer observed_first_contentful_paint, 
			Long observed_navigation_start_ts, 
			Long observed_largest_contentful_paint_ts, 
			Integer observed_first_visual_change,
			Long observed_load_ts,
			Integer first_meaningful_paint,
			Integer observed_trace_end,
			Integer observed_first_meaningful_paint,
			Integer first_cpu_idle,
			Long observed_trace_end_ts,
			Long observed_first_meaningful_paint_ts,
			Integer observed_dom_content_loaded,
			Long observed_first_visual_change_ts,
			Integer interactive,
			Integer observed_navigation_start,
			Long observed_first_contentful_paint_ts,
			Long observed_last_visual_change_ts,
			Integer observed_load,
			Integer observed_largest_contentful_paint,
			Long observed_dom_content_loaded_ts,
			Integer observed_speed_index,
			Integer estimated_input_latency,
			Integer total_blocking_time,
			Integer observed_first_paint,
			Integer observed_last_visual_change,
			Boolean lcp_invalidated
	) {
		setFirstContentfulPaint(first_contentful_paint);
		setObservedFirstContentfulPaint(observed_first_contentful_paint);
		setEstimatedInputLatency(estimated_input_latency);
		setFirstCpuIdle(first_cpu_idle);
		setFirstMeaningfulPaint(first_meaningful_paint);
		setInteractive(interactive);
		setLcpInvalidated(lcp_invalidated);
		setObservedFirstMeaningfulPaint(observed_first_meaningful_paint);
		setObservedFirstVisualChangeTs(observed_first_visual_change_ts);
		setObservedDomContentLoaded(observed_dom_content_loaded);
		setObservedDomContentLoadedTs(observed_dom_content_loaded_ts);
		setObservedFirstContentfulPaint(observed_first_contentful_paint);
		setObservedFirstContentfulPaintTs(observed_first_contentful_paint_ts);
		setObservedFirstPaint(observed_first_paint);
		setObservedFirstPaintTs(observed_first_paint_ts);
		setObservedFirstVisualChange(observed_first_visual_change);
		setObservedLargestContentfulPaint(observed_largest_contentful_paint);
		setObservedLargestContentfulPaintTs(observed_largest_contentful_paint_ts);
		setObservedLastVisualChange(observed_last_visual_change);
		setObservedLastVisualChangeTs(observed_last_visual_change_ts);
		setObservedLoad(observed_load);
		setObservedLoadTs(observed_load_ts);
		setObservedNavigationStart(observed_navigation_start);
		setObservedNavigationStartTs(observed_navigation_start_ts);
		setObservedSpeedIndex(observed_speed_index);
		setObservedSpeedIndexTs(observed_speed_index_ts);
		setObservedTraceEnd(observed_trace_end);
		setObservedTraceEndTs(observed_trace_end_ts);
		setSpeedIndex(speed_index);
		setTotalBlockingTime(total_blocking_time);
		setCreatedDate(new Date());
	}

	public Integer getFirstContentfulPaint() {
		return first_contentful_paint;
	}

	public void setFirstContentfulPaint(Integer first_contentful_paint) {
		this.first_contentful_paint = first_contentful_paint;
	}

	public Long getObservedFirstPaintTs() {
		return observed_first_paint_ts;
	}

	public void setObservedFirstPaintTs(Long observed_first_paint_ts) {
		this.observed_first_paint_ts = observed_first_paint_ts;
	}

	public Integer getSpeedIndex() {
		return speed_index;
	}

	public void setSpeedIndex(Integer speed_index) {
		this.speed_index = speed_index;
	}

	public Long getObservedSpeedIndexTs() {
		return observed_speed_index_ts;
	}

	public void setObservedSpeedIndexTs(Long observed_speed_index_ts) {
		this.observed_speed_index_ts = observed_speed_index_ts;
	}

	public Integer getObservedFirstContentfulPaint() {
		return observed_first_contentful_paint;
	}

	public void setObservedFirstContentfulPaint(Integer observed_first_contentful_paint) {
		this.observed_first_contentful_paint = observed_first_contentful_paint;
	}

	public Long getObservedNavigationStartTs() {
		return observed_navigation_start_ts;
	}

	public void setObservedNavigationStartTs(Long observed_navigation_start_ts) {
		this.observed_navigation_start_ts = observed_navigation_start_ts;
	}

	public Long getObservedLargestContentfulPaintTs() {
		return observed_largest_contentful_paint_ts;
	}

	public void setObservedLargestContentfulPaintTs(Long observed_largest_contentful_paint_ts) {
		this.observed_largest_contentful_paint_ts = observed_largest_contentful_paint_ts;
	}

	public Integer getObservedFirstVisualChange() {
		return observed_first_visual_change;
	}

	public void setObservedFirstVisualChange(Integer observed_first_visual_change) {
		this.observed_first_visual_change = observed_first_visual_change;
	}

	public Long getObservedLoadTs() {
		return observed_load_ts;
	}

	public void setObservedLoadTs(Long observed_load_ts) {
		this.observed_load_ts = observed_load_ts;
	}

	public Integer getFirstMeaningfulPaint() {
		return first_meaningful_paint;
	}

	public void setFirstMeaningfulPaint(Integer first_meaningful_paint) {
		this.first_meaningful_paint = first_meaningful_paint;
	}

	public Integer getObservedTraceEnd() {
		return observed_trace_end;
	}

	public void setObservedTraceEnd(Integer observed_trace_end) {
		this.observed_trace_end = observed_trace_end;
	}

	public Integer getObservedFirstMeaningfulPaint() {
		return observed_first_meaningful_paint;
	}

	public void setObservedFirstMeaningfulPaint(Integer observed_first_meaningful_paint) {
		this.observed_first_meaningful_paint = observed_first_meaningful_paint;
	}

	public Integer getFirstCpuIdle() {
		return first_cpu_idle;
	}

	public void setFirstCpuIdle(Integer first_cpu_idle) {
		this.first_cpu_idle = first_cpu_idle;
	}

	public Long getObservedTraceEndTs() {
		return observed_trace_end_ts;
	}

	public void setObservedTraceEndTs(Long observed_trace_end_ts) {
		this.observed_trace_end_ts = observed_trace_end_ts;
	}

	public Long getObservedFirstMeaningfulPaintTs() {
		return observed_first_meaningful_paint_ts;
	}

	public void setObservedFirstMeaningfulPaintTs(Long observed_first_meaningful_paint_ts) {
		this.observed_first_meaningful_paint_ts = observed_first_meaningful_paint_ts;
	}

	public Integer getObservedDomContentLoaded() {
		return observed_dom_content_loaded;
	}

	public void setObservedDomContentLoaded(Integer observed_dom_content_loaded) {
		this.observed_dom_content_loaded = observed_dom_content_loaded;
	}

	public Long getObservedFirstVisualChangeTs() {
		return observed_first_visual_change_ts;
	}

	public void setObservedFirstVisualChangeTs(Long observed_first_visual_change_ts) {
		this.observed_first_visual_change_ts = observed_first_visual_change_ts;
	}

	public Integer getInteractive() {
		return interactive;
	}

	public void setInteractive(Integer interactive) {
		this.interactive = interactive;
	}

	public Integer getObservedNavigationStart() {
		return observed_navigation_start;
	}

	public void setObservedNavigationStart(Integer observed_navigation_start) {
		this.observed_navigation_start = observed_navigation_start;
	}

	public Long getObservedFirstContentfulPaintTs() {
		return observed_first_contentful_paint_ts;
	}

	public void setObservedFirstContentfulPaintTs(Long observed_first_contentful_paint_ts) {
		this.observed_first_contentful_paint_ts = observed_first_contentful_paint_ts;
	}

	public Long getObservedLastVisualChangeTs() {
		return observed_last_visual_change_ts;
	}

	public void setObservedLastVisualChangeTs(Long observed_last_visual_change_ts) {
		this.observed_last_visual_change_ts = observed_last_visual_change_ts;
	}

	public Integer getObservedLoad() {
		return observed_load;
	}

	public void setObservedLoad(Integer observed_load) {
		this.observed_load = observed_load;
	}

	public Integer getObservedLargestContentfulPaint() {
		return observed_largest_contentful_paint;
	}

	public void setObservedLargestContentfulPaint(Integer observed_largest_contentful_paint) {
		this.observed_largest_contentful_paint = observed_largest_contentful_paint;
	}

	public Long getObservedDomContentLoadedTs() {
		return observed_dom_content_loaded_ts;
	}

	public void setObservedDomContentLoadedTs(Long observed_dom_content_loaded_ts) {
		this.observed_dom_content_loaded_ts = observed_dom_content_loaded_ts;
	}

	public Integer getObservedSpeedIndex() {
		return observed_speed_index;
	}

	public void setObservedSpeedIndex(Integer observed_speed_index) {
		this.observed_speed_index = observed_speed_index;
	}

	public Integer getEstimatedInputLatency() {
		return estimated_input_latency;
	}

	public void setEstimatedInputLatency(Integer estimated_input_latency) {
		this.estimated_input_latency = estimated_input_latency;
	}

	public Integer getTotalBlockingTime() {
		return total_blocking_time;
	}

	public void setTotalBlockingTime(Integer total_blocking_time) {
		this.total_blocking_time = total_blocking_time;
	}

	public Integer getObservedFirstPaint() {
		return observed_first_paint;
	}

	public void setObservedFirstPaint(Integer observed_first_paint) {
		this.observed_first_paint = observed_first_paint;
	}

	public Integer getObservedLastVisualChange() {
		return observed_last_visual_change;
	}

	public void setObservedLastVisualChange(Integer observed_last_visual_change) {
		this.observed_last_visual_change = observed_last_visual_change;
	}

	public Boolean getLcpInvalidated() {
		return lcp_invalidated;
	}

	public void setLcpInvalidated(Boolean lcp_invalidated) {
		this.lcp_invalidated = lcp_invalidated;
	}

}
