package com.looksee.models.dto.integration;

public class User {
	private String email;
	private String name;
	private String id;
	private String company_name;
	private String company_domain;
	
	public User(String email, 
				String name) {
		setEmail(email);
		setName(name);
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_domain() {
		return company_domain;
	}
	public void setCompany_domain(String company_domain) {
		this.company_domain = company_domain;
	}
}
