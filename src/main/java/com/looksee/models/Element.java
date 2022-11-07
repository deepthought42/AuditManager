package com.looksee.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Relationship;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.models.enums.ElementClassification;
import com.looksee.models.rules.Rule;


/**
 * Contains all the pertinent information for an element on a page. A ElementState
 *  may be a Parent and/or child of another ElementState. This heirarchy is not
 *  maintained by ElementState though. 
 */
public class Element extends LookseeObject implements Comparable<Element> {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Element.class);

	private String name;
	private String xpath;
	private String css_selector;
	private String classification;
	private String template;
	private String text;
	
	@Properties
	private Map<String, String> attributes = new HashMap<>();
	
	@Properties
	private Map<String, String> pre_render_css_values = new HashMap<>();
	
	@Relationship(type = "HAS", direction = Relationship.OUTGOING)
	private Set<Rule> rules = new HashSet<>();

	@Relationship(type = "HAS_CHILD", direction = Relationship.OUTGOING)
	private List<Element> child_elements = new ArrayList<>();
	
	@Relationship()
	private List<ElementState> element_states = new ArrayList<>();

	public Element(){
		super();
	}
	
	/**
	 * 
	 * @param text
	 * @param xpath
	 * @param name
	 * @param attributes
	 * @param css_map
	 * @pre attributes != null
	 * @pre css_map != null
	 * @pre xpath != null
	 * @pre name != null
	 * @pre screenshot_url != null
	 * @pre !screenshot_url.isEmpty()
	 */
	public Element(String text, String xpath, String name, Map<String, String> attributes, 
			Map<String, String> css_map, String inner_html, String outer_html){
		super();
		assert attributes != null;
		assert css_map != null;
		assert xpath != null;
		assert name != null;
		
		setText(text);
		setName(name);
		setXpath(xpath);
		setPreRenderCssValues(css_map);
		setCssSelector("");
		setTemplate(outer_html);
		setRules(new HashSet<>());
		setClassification(ElementClassification.LEAF);
		setAttributes(attributes);
		setKey(generateKey());
	}
	
	/**
	 * 
	 * @param text
	 * @param xpath
	 * @param name
	 * @param attributes
	 * @param css_map
	 * @param outer_html TODO
	 * @pre xpath != null
	 * @pre name != null
	 * @pre screenshot_url != null
	 * @pre !screenshot_url.isEmpty()
	 * @pre outer_html != null;
	 * @pre assert !outer_html.isEmpty()
	 */
	public Element(String text, String xpath, String name, 
					Map<String, String> attributes, 
					Map<String, String> css_map, 
					String inner_html, 
					ElementClassification classification, String outer_html){
		assert name != null;
		assert xpath != null;
		assert outer_html != null;
		assert !outer_html.isEmpty();
		
		setText(text);
		setName(name);
		setXpath(xpath);
		setAttributes(attributes);
		
		setPreRenderCssValues(css_map);
		setCssSelector("");
		setTemplate(outer_html);
		setRules(new HashSet<>());
		setClassification(classification);
		setKey(generateKey());
	}
	
	/**
	 * checks if css properties match between {@link WebElement elements}
	 * 
	 * @param elem
	 * @return whether attributes match or not
	 */
	public boolean cssMatches(Element elem){
		for(String propertyName : pre_render_css_values.keySet()){
			if(propertyName.contains("-moz-") || propertyName.contains("-webkit-") || propertyName.contains("-o-") || propertyName.contains("-ms-")){
				continue;
			}
			if(!pre_render_css_values.get(propertyName).equals(elem.getPreRenderCssValues().get(propertyName))){
				return false;
			}
		}
		return true;
	}
	
	/** GETTERS AND SETTERS  **/
	public boolean isPartOfForm() {
		return this.getXpath().contains("form");
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String tagName) {
		this.name = tagName;
	}

	public String getXpath() {
		return xpath;
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public Map<String, String> getPreRenderCssValues() {
		return pre_render_css_values;
	}

	public void setPreRenderCssValues(Map<String, String> css_values) {
		this.pre_render_css_values = css_values;
	}
	
	public Set<Rule> getRules(){
		return this.rules;
	}

	public void setRules(Set<Rule> rules) {
		this.rules = rules;
	}

	public void addRule(Rule rule) {
		boolean exists = false;
		for(Rule existing_rule : this.rules){
			if(existing_rule.getKey().equals(rule.getKey())){
				exists = true;
			}
		}
		if(!exists){
			this.rules.add(rule);
		}
	}
	
	/**
	 * Generates a key using both path and result in order to guarantee uniqueness of key as well 
	 * as easy identity of {@link Test} when generated in the wild via discovery
	 * 
	 * @return
	 */
	public String generateKey() {
		String key = "";
		List<String> properties = new ArrayList<>(getPreRenderCssValues().keySet());
		Collections.sort(properties);
		for(String style : properties) {
			key += getPreRenderCssValues().get(style);
		}
		return "element"+org.apache.commons.codec.digest.DigestUtils.sha256Hex(key+this.getTemplate()+this.getXpath());
	}
	

	/**
	 * Prints this elements xpath
	 */
	public String toString(){
		return this.xpath;
	}

	/**
	 * Checks if {@link Element elements} are equal
	 * 
	 * @param elem
	 * @return whether or not elements are equal
	 */
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
        if (!(o instanceof Element)) return false;
        
        Element that = (Element)o;
		return this.getKey().equals(that.getKey());
	}


	public Element clone() {
		Element page_elem = new Element();
		page_elem.setPreRenderCssValues(this.getPreRenderCssValues());
		page_elem.setKey(this.getKey());
		page_elem.setName(this.getName());
		page_elem.setXpath(this.getXpath());
		page_elem.setTemplate(this.getTemplate());
		
		return page_elem;
	}

	@Override
	public int compareTo(Element o) {
        return this.getKey().compareTo(o.getKey());
	}

	public String getCssSelector() {
		return css_selector;
	}

	public void setCssSelector(String css_selector) {
		this.css_selector = css_selector;
	}

	public String getTemplate(){
		return this.template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isLeaf() {
		return getClassification().equals(ElementClassification.LEAF);
	}

	public ElementClassification getClassification() {
		return ElementClassification.create(classification);
	}

	public void setClassification(ElementClassification classification) {
		this.classification = classification.toString();
	}
	
	public List<Element> getChildElements() {
		return child_elements;
	}

	public void setChildElements(List<Element> child_elements) {
		this.child_elements = child_elements;
	}
	
	public void addChildElement(Element child_element) {
		this.child_elements.add(child_element);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String attr_name) {
		return attributes.get(attr_name);
	}
	
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addAttribute(String attribute_name, String attribute_value) {
		this.attributes.put(attribute_name, attribute_value);
	}
}
