package com.looksee.models.dto;

import com.looksee.models.competitiveanalysis.brand.Brand;


/**
 * 
 */
public class CompetitorDto {
	private long id;
	private String company_name;
	private String url;
	private String industry;
	private boolean analysis_running;
	private Brand brand;
	
	public CompetitorDto(){	}

	/**
	 *
	 * @param id TODO
	 * @param username
	 * @param customer_token
	 * @param subscription_token
	 * @pre users != null
	 */
	public CompetitorDto(
			long id, 
			String company_name, 
			String url,
			String industry,
			boolean analysis_running, 
			Brand brand
	){
		setId(id);
		setCompanyName(company_name);
		setUrl(url);
		setIndustry(industry);
		setAnalysisRunning(analysis_running);
		setBrand(brand);
	}

	public String getCompanyName() {
		return company_name;
	}

	public void setCompanyName(String company_name) {
		this.company_name = company_name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public boolean isAnalysisRunning() {
		return analysis_running;
	}

	public void setAnalysisRunning(boolean is_analysis_running) {
		this.analysis_running = is_analysis_running;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
