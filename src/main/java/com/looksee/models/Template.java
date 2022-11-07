package com.looksee.models;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.enums.TemplateType;

/**
 * 		A Template is defined as a semi-generic string that matches a set of {@link Element}s
 */
@NodeEntity
public class Template implements Persistable{
	@GeneratedValue
    @Id
	private long id;
	private String type;
	private String key;
	private String template;
	
	@Relationship(type = "MATCHES")
	private List<Element> elements;
	
	public Template(){
		setType(TemplateType.UNKNOWN);
		setTemplate("");
		setElements(new ArrayList<>());
		setKey(generateKey());
	}
	
	public Template(TemplateType type, String template){
		setType(type);
		setTemplate(template);
		setElements(new ArrayList<>());
		setKey(generateKey());
	}
	
	@Override
	public String generateKey() {
		return type+org.apache.commons.codec.digest.DigestUtils.sha256Hex(template);
	}

	public TemplateType getType() {
		return TemplateType.create(type);
	}

	public void setType(TemplateType type) {
		this.type = type.toString();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	
	public long getId(){
		return this.id;
	}
}
