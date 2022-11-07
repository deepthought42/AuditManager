package com.looksee.models;

import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Defines a name and color used to group {@link Test}s
 */
@NodeEntity
public class Group implements Persistable{
	
	@GeneratedValue
    @Id
	private Long id;
	
	private String key;
	private String name;
	private String description;
	
	

	public Group(){}
	
	/**
	 * Construct a new grouping	
	 * 
	 * @param name 		name of the group
	 * @param test		{@link List} of {@link Test}s
	 * @param description describes group
	 */
	public Group(String name){
		setName(name);
		setDescription("");
		setKey(generateKey());
	}
	
	/**
	 * Construct a new grouping	
	 * 
	 * @param name 		name of the group
	 * @param test		{@link List} of {@link Test}s
	 * @param description describes group
	 */
	public Group(String name, String desc){
		setName(name);
		setDescription(desc);
		setKey(generateKey());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String formatted_name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		this.name = formatted_name;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	

	/**
	 * {@inheritDoc}
	 */
	public String generateKey() {
		return "group:"+getName().toLowerCase();
	}
	
}
