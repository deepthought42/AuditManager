package com.looksee.models;

import java.util.Date;

/**
 * 
 */
public class AccountUsage {
	private int discovery_limit;
	private int discoveries_used;
	private int test_limit;
	private int tests_used;
	private int total_discovered_test_count;
	
	private int domain_discoveries;
	private int domain_total_tests_run;
	private int domain_total_discovered_test_count;
	
	private Date current_discovery_start;
	private long discovery_run_time;
	private Date timestamp_of_last_discovered_test;
	
	public AccountUsage(int discovery_limit, int discoveries_used, int test_limit, int tests_used){
		this.setDiscoveryLimit(discovery_limit);
		this.setDiscoveriesUsed(discoveries_used);
		this.setTestLimit(test_limit);
		this.setTestsUsed(tests_used);
	}

	public AccountUsage(int discoveries_used, int tests_used, int total_tests_discovered, int domain_discovery_count, 
			int domain_total_tests, int domain_total_discovered_tests, Date discovery_start_time, long discovery_run_time, Date date_of_last_discovered_test){
		this.setDiscoveryLimit(discovery_limit);
		this.setDiscoveriesUsed(discoveries_used);
		this.setTestLimit(test_limit);
		this.setTestsUsed(tests_used);
		this.setTotalDiscoveredTestCount(total_tests_discovered);
		this.setDomainDiscoveryCount(domain_discovery_count);
		this.setDomainTotalDiscoveredTestCount(domain_total_discovered_tests);
		this.setDomainTotalTestsRun(domain_total_tests);
		this.setCurrentDiscoveryStart(discovery_start_time);
		this.setDiscoveryRunTime(discovery_run_time);
		this.setTimestampOfLastDiscoveredTest(date_of_last_discovered_test);
	}
	
	public int getDiscoveryLimit() {
		return discovery_limit;
	}

	public void setDiscoveryLimit(int discovery_limit) {
		this.discovery_limit = discovery_limit;
	}

	public int getDiscoveriesUsed() {
		return discoveries_used;
	}

	public void setDiscoveriesUsed(int discoveries_used) {
		this.discoveries_used = discoveries_used;
	}

	public int getTestLimit() {
		return test_limit;
	}

	public void setTestLimit(int test_limit) {
		this.test_limit = test_limit;
	}

	public int getTestsUsed() {
		return tests_used;
	}

	public void setTestsUsed(int tests_used) {
		this.tests_used = tests_used;
	}

	public int getTotalDiscoveredTestCount() {
		return total_discovered_test_count;
	}

	public void setTotalDiscoveredTestCount(int total_discovered_test_count) {
		this.total_discovered_test_count = total_discovered_test_count;
	}

	public int getDomainDiscoveryCount() {
		return domain_discoveries;
	}

	public void setDomainDiscoveryCount(int domain_discoveries) {
		this.domain_discoveries = domain_discoveries;
	}

	public int getDomainTotalTestsRun() {
		return domain_total_tests_run;
	}

	public void setDomainTotalTestsRun(int domain_total_tests_run) {
		this.domain_total_tests_run = domain_total_tests_run;
	}

	public int getDomainTotalDiscoveredTestCount() {
		return domain_total_discovered_test_count;
	}

	public void setDomainTotalDiscoveredTestCount(int domain_total_discovered_test_count) {
		this.domain_total_discovered_test_count = domain_total_discovered_test_count;
	}

	public Date getCurrentDiscoveryStart() {
		return current_discovery_start;
	}

	public void setCurrentDiscoveryStart(Date current_discovery_start) {
		this.current_discovery_start = current_discovery_start;
	}

	public long getDiscoveryRunTime() {
		return discovery_run_time;
	}

	public void setDiscoveryRunTime(long discovery_run_time) {
		this.discovery_run_time = discovery_run_time;
	}

	public Date getTimestampOfLastDiscoveredTest() {
		return timestamp_of_last_discovered_test;
	}

	public void setTimestampOfLastDiscoveredTest(Date timestamp_of_last_discovered_test) {
		this.timestamp_of_last_discovered_test = timestamp_of_last_discovered_test;
	}
}
