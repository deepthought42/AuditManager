package com.looksee.models.audit.performance;


/**
 * 
 */
public class DiagnosticDetail extends AuditDetail {

	private Integer num_stylesheets;
	private Double throughput;
	private Integer num_tasks_over_10ms;
	private Integer num_tasks_over_25ms;
	private Integer num_tasks_over_50ms;
	private Integer num_tasks_over_100ms;
	private Integer num_tasks_over_500ms;
	private Integer num_requests;
	private Double total_task_time;
	private Integer main_document_transfer_size;
	private Integer total_byte_weight;
	private Integer num_tasks;
	private Double rtt;
	private Double maxRtt;
	private Integer numFonts;
	private Integer numScripts;
	
	public DiagnosticDetail() {}
	
	public DiagnosticDetail(
			Integer num_stylesheets, 
			Double throughput, 
			Integer num_tasks_over_10ms,
			Integer num_tasks_over_25ms,
			Integer num_tasks_over_50ms,
			Integer num_tasks_over_100ms,
			Integer num_tasks_over_500ms,
			Integer num_requests,
			Double total_task_time,
			Integer main_document_transfer_size,
			Integer total_byte_weight,
			Integer num_tasks,
			Double rtt,
			Double max_rtt,
			Integer num_fonts,
			Integer num_scripts) {
		setNumStylesheets(num_stylesheets);
		setThroughput(throughput);
		setNumTasksOver10ms(num_tasks_over_10ms);
		setNumTasksOver25ms(num_tasks_over_25ms);
		setNumTasksOver50ms(num_tasks_over_50ms);
		setNumTasksOver100ms(num_tasks_over_100ms);
		setNumTasksOver500ms(num_tasks_over_500ms);
		setNumRequests(num_requests);
		setTotalTaskTime(total_task_time);
		setMainDocumentTransferSize(main_document_transfer_size);
		setTotalByteWeight(total_byte_weight);
		setNumTasks(num_tasks);
		setRtt(rtt);
		setMaxRtt(max_rtt);
		setNumFonts(num_fonts);
		setNumScripts(num_scripts);
	}

	public Integer getNumStylesheets() {
		return num_stylesheets;
	}

	public void setNumStylesheets(Integer num_stylesheets) {
		this.num_stylesheets = num_stylesheets;
	}

	public Double getThroughput() {
		return throughput;
	}

	public void setThroughput(Double throughput) {
		this.throughput = throughput;
	}

	public Integer getNumTasksOver10ms() {
		return num_tasks_over_10ms;
	}

	public void setNumTasksOver10ms(Integer num_tasks_over_10ms) {
		this.num_tasks_over_10ms = num_tasks_over_10ms;
	}

	public Integer getNumTasksOver25ms() {
		return num_tasks_over_25ms;
	}

	public void setNumTasksOver25ms(Integer num_tasks_over_25ms) {
		this.num_tasks_over_25ms = num_tasks_over_25ms;
	}

	public Integer getNumTasksOver50ms() {
		return num_tasks_over_50ms;
	}

	public void setNumTasksOver50ms(Integer num_tasks_over_50ms) {
		this.num_tasks_over_50ms = num_tasks_over_50ms;
	}

	public Integer getNumTasksOver100ms() {
		return num_tasks_over_100ms;
	}

	public void setNumTasksOver100ms(Integer num_tasks_over_100ms) {
		this.num_tasks_over_100ms = num_tasks_over_100ms;
	}

	public Integer getNumTasksOver500ms() {
		return num_tasks_over_500ms;
	}

	public void setNumTasksOver500ms(Integer num_tasks_over_500ms) {
		this.num_tasks_over_500ms = num_tasks_over_500ms;
	}

	public Integer getNumRequests() {
		return num_requests;
	}

	public void setNumRequests(Integer num_requests) {
		this.num_requests = num_requests;
	}

	public Double getTotalTaskTime() {
		return total_task_time;
	}

	public void setTotalTaskTime(Double total_task_time) {
		this.total_task_time = total_task_time;
	}

	public Integer getMainDocumentTransferSize() {
		return main_document_transfer_size;
	}

	public void setMainDocumentTransferSize(Integer main_document_transfer_size) {
		this.main_document_transfer_size = main_document_transfer_size;
	}

	public Integer getTotalByteWeight() {
		return total_byte_weight;
	}

	public void setTotalByteWeight(Integer total_byte_weight) {
		this.total_byte_weight = total_byte_weight;
	}

	public Integer getNumTasks() {
		return num_tasks;
	}

	public void setNumTasks(Integer num_tasks) {
		this.num_tasks = num_tasks;
	}

	public Double getRtt() {
		return rtt;
	}

	public void setRtt(Double rtt) {
		this.rtt = rtt;
	}

	public Double getMaxRtt() {
		return maxRtt;
	}

	public void setMaxRtt(Double maxRtt) {
		this.maxRtt = maxRtt;
	}

	public Integer getNumFonts() {
		return numFonts;
	}

	public void setNumFonts(Integer numFonts) {
		this.numFonts = numFonts;
	}

	public Integer getNumScripts() {
		return numScripts;
	}

	public void setNumScripts(Integer numScripts) {
		this.numScripts = numScripts;
	}
}
