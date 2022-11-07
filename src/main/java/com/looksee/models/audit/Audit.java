package com.looksee.models.audit;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.LookseeObject;
import com.looksee.models.enums.AuditCategory;
import com.looksee.models.enums.AuditLevel;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.AuditSubcategory;

/**
 * Defines the globally required fields for all audits
 */
public class Audit extends LookseeObject {

	private String category;
	private String subcategory;
	private String name; // name of the audit
	private String level;
	private int points;      //scoring
	private int total_possible_points;      //scoring
	private String url;
	private boolean is_accessibility;
	private String description;
	private String why_it_matters;
	
	@Relationship(type = "HAS")
	private Set<UXIssueMessage> messages;
	
	private Set<String> labels;

	
	/**
	 * Construct empty action object
	 */
	public Audit(){
		super();
		setMessages(new HashSet<>());
	}
	
	/**
	 * 
	 * @param category
	 * @param subcategory
	 * @param points
	 * @param ux_issues
	 * @param level
	 * @param total_possible_points
	 * @param url TODO
	 * @param why_it_matters TODO
	 * @param description TODO
	 * @param isAccessibility TODO
	 */
	public Audit(
			AuditCategory category, 
			AuditSubcategory subcategory,
			AuditName name,
			int points, 
			Set<UXIssueMessage> ux_issues, 
			AuditLevel level, 
			int total_possible_points,
			String url, 
			String why_it_matters, 
			String description, 
			boolean is_accessibility
	) {
		super();
		
		assert category != null;
		assert subcategory != null;
		assert ux_issues != null;
		assert level != null;
		
		setName(name);
		setCategory(category);
		setSubcategory(subcategory);
		setPoints(points);
		setTotalPossiblePoints(total_possible_points);
		setMessages(ux_issues);
		setCreatedAt(LocalDateTime.now());
		setLevel(level);
		setUrl(url);
		setWhyItMatters(why_it_matters);
		setDescription(description);
		setAccessiblity(is_accessibility);
		setKey(generateKey());
	}

	public Audit clone() {
		return new Audit(getCategory(), getSubcategory(), getName(), getPoints(), getMessages(), getLevel(), getTotalPossiblePoints(), getUrl(), getWhyItMatters(), getDescription(), isAccessiblity());
	}

	/**
	 * @return string of hashCodes identifying unique fingerprint of object by the contents of the object
	 */
	public String generateKey() {
		return "audit"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.getName().toString()+this.getCategory().toString()+this.getLevel()+getPoints()+getTotalPossiblePoints()+getCreatedAt().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return this.getKey();
	}

	public AuditCategory getCategory() {
		return AuditCategory.create(category);
	}
	
	public void setCategory(AuditCategory category) {
		this.category = category.toString();
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public AuditName getName() {
		return AuditName.create(name);
	}
	
	public void setName(AuditName subcategory) {
		this.name = subcategory.getShortName();
	}
	
	public AuditLevel getLevel() {
		return AuditLevel.create(level);
	}

	public void setLevel(AuditLevel level) {
		this.level = level.toString();
	}

	public int getTotalPossiblePoints() {
		return total_possible_points;
	}

	public void setTotalPossiblePoints(int total_possible_points) {
		this.total_possible_points = total_possible_points;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AuditSubcategory getSubcategory() {
		return AuditSubcategory.create(subcategory);
	}

	public void setSubcategory(AuditSubcategory subcategory) {
		this.subcategory = subcategory.toString();
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getWhyItMatters() {
		return why_it_matters;
	}

	public void setWhyItMatters(String why_it_matters) {
		this.why_it_matters = why_it_matters;
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}


	public Set<UXIssueMessage> getMessages() {
		return messages;
	}

	public void setMessages(Set<UXIssueMessage> messages) {
		this.messages = messages;
	}

	public boolean isAccessiblity() {
		return is_accessibility;
	}

	public void setAccessiblity(boolean is_accessibility) {
		this.is_accessibility = is_accessibility;
	}
}
