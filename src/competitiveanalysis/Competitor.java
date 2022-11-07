package com.looksee.models.competitiveanalysis;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.LookseeObject;
import com.looksee.models.competitiveanalysis.brand.Brand;


/**
 * Defines the type of package paid for, which domains are registered and which Users belong to the account
 */
@NodeEntity
public class Competitor extends LookseeObject{

	private String company_name;
	private String url;
	private String industry;
	
	@Relationship("USES")
	private Brand brand;
	
	public Competitor(){
		super();
	}

	/**
	 *
	 * @param username
	 * @param customer_token
	 * @param subscription_token
	 *
	 * @pre users != null
	 */
	public Competitor(
			String company_name, 
			String url, 
			String industry
	){
		super();
		setCompanyName(company_name);
		setUrl(url);
		setIndustry(industry);
	}

	
	@Override
	public String generateKey() {
		return "competitor::" + org.apache.commons.codec.digest.DigestUtils.sha256Hex( this.getUrl() );
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
}
