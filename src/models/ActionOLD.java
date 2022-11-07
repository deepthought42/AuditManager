package com.looksee.models;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Defines an action in name only
 */
@NodeEntity
public class ActionOLD extends LookseeObject {
	
	private String name;
	private String value;
	
	/**
	 * Construct empty action object
	 */
	public ActionOLD(){}
	
	/**
	 * 
	 * @param action_name
	 */
	public ActionOLD(String action_name) {
		this.name = action_name;
		this.value = "";
		this.setKey(generateKey());
	}
	
	/**
	 * 
	 * @param action_name
	 */
	public ActionOLD(String action_name, String value) {
		setName(action_name);
		setValue(value);
		this.setKey(generateKey());
	}
	
	/**
	 * @return the name of this action
	 */
	public String getName(){
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public ActionOLD clone() {
		ActionOLD action_clone = new ActionOLD(this.getName(), this.getValue());
		return action_clone;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateKey() {
		return "action:"+getName() + ":"+ getValue();
	}
}
