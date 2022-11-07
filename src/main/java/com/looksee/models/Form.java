package com.looksee.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.ogm.annotation.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.looksee.models.audit.performance.BugMessage;
import com.looksee.models.enums.FormType;

/**
 * Represents a form tag and the encompassed inputs in a web browser
 */
public class Form extends LookseeObject{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Form.class);

	private Long memory_id;
	private String name;
    
	private String type;
	
	@Relationship(type = "HAS")
	private List<BugMessage> bug_messages;
	
	@Relationship(type = "DEFINED_BY")
	private ElementState form_tag;
	
	@Relationship(type = "HAS")
	private List<ElementState> form_fields;
	
	@Relationship(type = "HAS_SUBMIT")
	private ElementState submit_field;
	
	public Form(){	}
	
	public Form(ElementState form_tag, 
				List<ElementState> form_fields, 
				ElementState submit_field, 
				String name){
		setFormTag(form_tag);
		setFormFields(form_fields);
		setSubmitField(submit_field);
		setType(determineFormType());
		log.warn("FORM TYPE IDENTIFIED :: "+getType());
		setName(name);
		setKey(generateKey());
	}
	
	/**
	 * Generates key for form based on element within it and the key of the form tag itself
	 * 
	 * @return
	 */
	@Override
	public String generateKey() {		
		return "form"+getFormTag();
	}

	/**
	 * Returns the {@link FormType} of the form based on attribute values on the form tag
	 * 
	 * @return {@link FormType}
	 */
	private FormType determineFormType(){
		Map<String, String> attributes = this.form_tag.getAttributes();
		for(String attr: attributes.keySet()){
			String vals = attributes.get(attr);
			if(vals.contains("register") || (vals.contains("sign") && vals.contains("up"))){
				return FormType.REGISTRATION;
			}
			else if(vals.contains("login") || (vals.contains("sign") && vals.contains("in"))){
				return FormType.LOGIN;
			}
			else if(vals.contains("search")){
				return FormType.SEARCH;
			}
			else if(vals.contains("reset") && vals.contains("password")){
				return FormType.PASSWORD_RESET;
			}
			else if(vals.contains("payment") || vals.contains("credit")){
				return FormType.PAYMENT;
			}
		}
		

		if(submit_field != null && (submit_field.getAllText().toLowerCase().contains("login") 
				|| submit_field.getAllText().toLowerCase().contains("sign-in") 
				|| submit_field.getAllText().toLowerCase().contains("sign in"))) {
			return FormType.LOGIN;
		}
		else if(submit_field != null && (submit_field.getAllText().toLowerCase().contains("register") 
				|| submit_field.getAllText().toLowerCase().contains("sign-up") 
				|| submit_field.getAllText().toLowerCase().contains("sign up"))) {
			return FormType.REGISTRATION;
		}
		
		boolean contains_username = false;
		boolean contains_password = false;
		boolean contains_password_confirmation = false;
		for(ElementState element : form_fields) {
			Map<String, String> element_attributes = element.getAttributes();
			for(String attr_val: element_attributes.values()){
				if(attr_val.contains("username") || attr_val.contains("email")){
					contains_username = true;
				}
				else if(attr_val.contains("password") && !(attr_val.contains("confirmation") || (attr_val.contains("confirm") && attr_val.contains("password")))){
					contains_password = true;
				}
				else if(attr_val.contains("password") && (attr_val.contains("confirmation") || attr_val.contains("confirm"))){
					contains_password_confirmation = true;
				}
			}
		}
		
		if(contains_username && contains_password && !contains_password_confirmation) {
			return FormType.LOGIN;
		}
		else if(contains_username && contains_password && contains_password_confirmation) {
			return FormType.REGISTRATION;
		}
		
		return FormType.UNKNOWN;
	}
	
	/**
	 * Checks if {@link Form forms} are equal
	 * 
	 * @param elem
	 * @return whether or not elements are equal
	 */
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
        if (!(o instanceof Form)) return false;
        
        Form that = (Form)o;
		return this.getKey().equals(that.getKey());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ElementState> getFormFields() {
		return form_fields;
	}
	
	public boolean addFormField(ElementState form_field) {
		return this.form_fields.add(form_field);
	}
	
	public boolean addFormFields(List<ElementState> form_field) {
		return this.form_fields.addAll(form_field);
	}
	
	public void setFormFields(List<ElementState> form_fields) {
		this.form_fields = form_fields;
	}

	public ElementState getSubmitField() {
		return submit_field;
	}

	public void setSubmitField(ElementState submit_field) {
		this.submit_field = submit_field;
	}

	public ElementState getFormTag() {
		return form_tag;
	}

	public void setFormTag(ElementState form_tag) {
		this.form_tag = form_tag;
	}

	public FormType getType() {
		return FormType.valueOf(type.toUpperCase());
	}

	public void setType(FormType type) {
		this.type = type.toString();
	}

	public Long getMemoryId() {
		return memory_id;
	}

	public void setMemoryId(Long memory_id) {
		this.memory_id = memory_id;
	}
	
	@Override
	public Form clone(){
		return new Form(form_tag, form_fields, submit_field, name);
	}

	public List<BugMessage> getBugMessages() {
		return bug_messages;
	}

	public void setBugMessages(List<BugMessage> bug_messages) {
		if(this.bug_messages == null) {
			this.bug_messages = new ArrayList<>();
		}
		this.bug_messages = bug_messages;
	}
	
	public void addBugMessage(BugMessage bug_message) {
		if(this.bug_messages == null) {
			this.bug_messages = new ArrayList<>();
		}
		log.warn("bug meesages  :: "+this.bug_messages);
		this.bug_messages.add(bug_message);
	}

	public void removeBugMessage(BugMessage msg) {
		int idx = bug_messages.indexOf(msg);
		this.bug_messages.remove(idx);
	}
}
