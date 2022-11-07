package com.looksee.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.audit.AuditRecord;


/**
 * Defines the type of package paid for, which domains are registered and which Users belong to the account
 */
@NodeEntity
public class Account extends LookseeObject{

	private String user_id;
	private String email;
	private String customer_token;
	private String subscription_token;
	private String subscription_type;
	private String last_domain_url;
	private List<String> onboarded_steps;
	private String api_token;
	private String name;
	
	@Relationship(type = "HAS")
	private Set<Domain> domains = new HashSet<>();

	@Relationship(type = "HAS")
	private Set<AuditRecord> audits = new HashSet<>();

	public Account(){
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
	public Account(
			String user_id, 
			String email, 
			String customer_token, 
			String subscription_token
	){
		super();
		setUserId(user_id);
		setEmail(email);
		setCustomerToken(customer_token);
		setSubscriptionToken(subscription_token);
		setOnboardedSteps(new ArrayList<String>());
		setName("");
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCustomerToken() {
		return customer_token;
	}

	public void setCustomerToken(String customer_token) {
		this.customer_token = customer_token;
	}

	public String getSubscriptionToken() {
		return subscription_token;
	}

	public void setSubscriptionToken(String subscription_token) {
		this.subscription_token = subscription_token;
	}

	public void setLastDomain(String domain_url) {
		this.last_domain_url = domain_url;
	}

	public String getLastDomain(){
		return this.last_domain_url;
	}

	public List<String> getOnboardedSteps() {
		return onboarded_steps;
	}

	public void setOnboardedSteps(List<String> onboarded_steps) {
		if(onboarded_steps == null){
			this.onboarded_steps = new ArrayList<String>();
		}
		else{
			this.onboarded_steps = onboarded_steps;
		}
	}

	public void addOnboardingStep(String step_name) {
		if(!this.onboarded_steps.contains(step_name)){
			this.onboarded_steps.add(step_name);
		}
	}

	public Set<Domain> getDomains(){
		return this.domains;
	}

	public void setDomains(Set<Domain> domains){
		this.domains = domains;
	}

	public void addDomain(Domain domain) {
		this.domains.add(domain);
	}

	public void removeDomain(Domain domain) {
		boolean domain_found = false;
		for(Domain curr_domain : this.domains){
			if(curr_domain.getKey().equals(domain.getKey())){
				domain_found = true;
				break;
			}
		}

		if(domain_found){
			this.domains.remove(domain);
		}
	}

	public Set<AuditRecord> getAuditRecords() {
		return audits;
	}

	public void setAuditRecords(Set<AuditRecord> audits) {
		this.audits = audits;
	}

	public void addAuditRecord(AuditRecord record) {
		this.audits.add(record);
	}

	public String getSubscriptionType() {
		return subscription_type;
	}

	public void setSubscriptionType(String subscription_type) {
		this.subscription_type = subscription_type;
	}

	public String getApiToken() {
		return api_token;
	}

	public void setApiToken(String api_token) {
		this.api_token = api_token;
	}

	public String getUserId() {
		return user_id;
  	}

  	public void setUserId(String user_id) {
  		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String generateKey() {
		return UUID.randomUUID().toString();
	}
}
