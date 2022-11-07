package com.looksee.models.journeys;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.looksee.models.ElementState;
import com.looksee.models.PageState;
import com.looksee.models.TestUser;
import com.looksee.models.enums.Action;

/**
 * A Step is the increment of work that start with a {@link PageState} contians an {@link ElementState} 
 * 	 that has an {@link Action} performed on it and results in an end {@link PageState}
 */
@NodeEntity
public class LoginStep extends Step {

	@Relationship(type = "USES")
	private TestUser test_user;
	
	@Relationship(type = "USERNAME_INPUT")
	private ElementState username_element;
	
	@Relationship(type = "PASSWORD_INPUT")
	private ElementState password_element;
	
	@Relationship(type = "SUBMIT")
	private ElementState submit_element;

	public LoginStep() {}
	
	public LoginStep(PageState start_page,
					 PageState end_page,
					 ElementState username_element,
					 ElementState password_element,
					 ElementState submit_btn,
					 TestUser user) {
		setStartPage(start_page);
		setEndPage(end_page);
		setUsernameElement(username_element);
		setPasswordElement(password_element);
		setSubmitElement(submit_btn);
		setTestUser(user);
		setKey(generateKey());
	}
	
	
	public ElementState getUsernameElement() {
		return username_element;
	}
	
	public void setUsernameElement(ElementState username_input) {
		this.username_element = username_input;
	}
	
	public ElementState getPasswordElement() {
		return password_element;
	}
	
	public void setPasswordElement(ElementState password_input) {
		this.password_element = password_input;
	}
	
	public TestUser getTestUser() {
		return test_user;
	}
	
	public void setTestUser(TestUser user) {
		this.test_user = user;
	}
	

	public ElementState getSubmitElement() {
		return submit_element;
	}

	public void setSubmitElement(ElementState submit_element) {
		this.submit_element = submit_element;
	}

	@Override
	public String generateKey() {
		String key = "";
		if(getStartPage() != null) {
			key += getStartPage().getId();
		}
		if(getEndPage() != null) {
			key += getEndPage().getId();
		}
		if(username_element != null) {
			key += username_element.getId();
		}
		if(password_element != null) {
			key += password_element.getId();
		}
		if(submit_element != null) {
			key += submit_element.getId();
		}
		if(test_user != null) {
			key += test_user.getId();
		}
		return "loginstep"+key;
	}
	
	
	@Override
	public String toString() {
		return "key = "+getKey()+",\n start_page = "+getStartPage()+"\n username element ="+getUsernameElement()+"\n password element ="+getPasswordElement()+"\n submit element = "+getSubmitElement()+"\n  end page = "+getEndPage();
	}
	
	@Override
	public LoginStep clone() {
		return new LoginStep(getStartPage(), getEndPage(), getUsernameElement(), getPasswordElement(), getSubmitElement(), getTestUser());
	}
}
