package com.looksee.dto;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.models.TestRecord;
import com.looksee.models.enums.TestStatus;

public class TestRecordDto {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(TestRecord.class);
	
	private String key;
	private Date ran_at;
	private String browser;
	private TestStatus status;
	private long run_time_length;
	private String test_key;
	private String result_key;
	
	//Empty constructor for spring
	public TestRecordDto(TestRecord record, String test_key){
		setKey(record.getKey());
		setRanAt(record.getRanAt());
		setBrowser(record.getBrowser());
		setStatus(record.getStatus());
		setRunTimeLength(record.getRunTime());
		setResultKey(record.getResult().getKey());
		setTestKey(test_key);
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		TestRecordDto.log = log;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getRanAt() {
		return ran_at;
	}

	public void setRanAt(Date ran_at) {
		this.ran_at = ran_at;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public TestStatus getStatus() {
		return status;
	}

	public void setStatus(TestStatus status) {
		this.status = status;
	}

	public long getRunTimeLength() {
		return run_time_length;
	}

	public void setRunTimeLength(long run_time_length) {
		this.run_time_length = run_time_length;
	}

	public String getResultKey() {
		return result_key;
	}

	public void setResultKey(String result_key) {
		this.result_key = result_key;
	}

	public String getTestKey() {
		return test_key;
	}

	public void setTestKey(String test_key) {
		this.test_key = test_key;
	}
}
