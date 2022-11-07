package com.looksee.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.audit.DomainAuditRecord;
import com.looksee.models.competitiveanalysis.Competitor;
import com.looksee.models.designsystem.DesignSystem;


/**
 * Encompasses a domain name as well as all {@link Test}s and {@link Group}s 
 * belong to this domain
 */
public class Domain extends LookseeObject{
	
	private String url;
	private String logo_url;
	private String entrypoint_url;
	private List<String> sitemap;
	
	@Relationship(type = "HAS")
	private List<PageState> pages;

	@Relationship(type = "HAS_TEST_USER")
	private Set<TestUser> test_users;
	
	@Relationship(type = "HAS_DOMAIN", direction = Relationship.INCOMING)
	private Account account;
	
	@Relationship(type = "HAS", direction = Relationship.INCOMING)
	private Set<DomainAuditRecord> audit_records;
	
	@Relationship(type="COMPETES_WTIH")
	private Set<Competitor> competitors;
	
	@Relationship(type="USES")
	private DesignSystem design_system;
	
	/**
	 * 
	 * 
	 * @param domain
	 * @param organization
	 */
	public Domain(){
		setTestUsers( new HashSet<>() );
		setPages( new ArrayList<>() );
		setAuditRecords(new HashSet<>());
		setSitemap(new ArrayList<>());
		setDesignSystem(new DesignSystem());
	}
	
	/**
	 * 
	 * @param protocol web protocol ("http", "https", "file", etc.)
	 * @param path landable url
	 * @param browser name of the browser ie. chrome, firefox, etc.
	 * @param logo_url url of logo image file
	 */
	public Domain( String protocol, 
				   String host, 
				   String path, 
				   String logo_url
	){
		setLogoUrl(logo_url);
		setUrl(host);
		setEntrypointUrl(host+path);
		setPages(new ArrayList<>());
		setAuditRecords(new HashSet<>());
		setDesignSystem(new DesignSystem());
		setKey(generateKey());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Domain){
			Domain domain = (Domain)o;
			if(domain.getUrl().equals(this.getUrl())){
				return true;
			}
		}
		
		return false;
	}

	public String getLogoUrl() {
		return logo_url;
	}

	public void setLogoUrl(String logo_url) {
		this.logo_url = logo_url;
	}

	public Set<TestUser> getTestUsers() {
		return test_users;
	}
	
	public void removeTestUser(TestUser test_user) {
		this.test_users.remove(test_user);
	}
	
	public boolean addTestUser(TestUser test_user) {
		return this.test_users.add(test_user);
	}

	public void setTestUsers(Set<TestUser> test_users) {
		this.test_users = new HashSet<TestUser>(test_users);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateKey() {
		return "domain"+org.apache.commons.codec.digest.DigestUtils.sha512Hex(getUrl());
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean addPage(PageState page) {
		//check if page state exists
		for(PageState state : this.getPages()){
			if(state.getKey().equals(page.getKey())){
				return false;
			}
		}
		
		return this.getPages().add(page);
	}

	public List<PageState> getPages() {
		return this.pages;
	}
	
	public void setPages(List<PageState> pages) {
		this.pages = pages;
	}

	public Set<DomainAuditRecord> getAuditRecords() {
		return audit_records;
	}

	public void setAuditRecords(Set<DomainAuditRecord> audit_records) {
		this.audit_records = audit_records;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getSitemap() {
		return sitemap;
	}

	public void setSitemap(List<String> sitemap) {
		this.sitemap = sitemap;
	}

	public String getEntrypointUrl() {
		return entrypoint_url;
	}

	public void setEntrypointUrl(String entrypoint_url) {
		this.entrypoint_url = entrypoint_url;
	}

	public Set<Competitor> getCompetitors() {
		return competitors;
	}

	public void setCompetitors(Set<Competitor> competitors) {
		this.competitors = competitors;
	}

	public DesignSystem getDesignSystem() {
		return design_system;
	}

	public void setDesignSystem(DesignSystem design_system) {
		this.design_system = design_system;
	}
}
