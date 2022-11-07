package com.looksee.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.api.exception.ExistingRuleException;
import com.looksee.models.Domain;
import com.looksee.models.Element;
import com.looksee.models.ElementState;
import com.looksee.models.repository.ElementStateRepository;
import com.looksee.models.rules.Rule;

import io.github.resilience4j.retry.annotation.Retry;

@Service
@Retry(name="neoforj")
public class ElementStateService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ElementStateService.class);

	@Autowired
	private ElementStateRepository element_repo;

	@Autowired
	private PageStateService page_state_service;
	
	/**
	 * 
	 * @param element
	 * @return
	 * 
	 * @pre element != null
	 */
	public ElementState save(ElementState element) {
		assert element != null;
		//ElementState element_record = element_repo.findByKey(element.getKey());
		//if(element_record == null){
			//iterate over attributes
		return element_repo.save(element);
		/*}
		else {
			element_record.setBackgroundColor(element.getBackgroundColor());
			element_record.setForegroundColor(element.getForegroundColor());
			element_repo.save(element_record);
		}
		return element_record;
		 */
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 * 
	 * @pre element != null
	 */
	public ElementState saveFormElement(ElementState element){
		assert element != null;
		ElementState element_record = element_repo.findByKey(element.getKey());
		if(element_record == null){			
			element_record = element_repo.save(element);
		}
		else{
			if(element.getScreenshotUrl() != null && !element.getScreenshotUrl().isEmpty()) {
				element_record.setScreenshotUrl(element.getScreenshotUrl());
				element_record.setXpath(element.getXpath());
	
				element_record = element_repo.save(element_record);
			}
		}
		return element_record;
	}

	public ElementState findByKey(String key){
		return element_repo.findByKey(key);
	}

	public void removeRule(String user_id, String element_key, String rule_key){
		element_repo.removeRule(user_id, element_key, rule_key);
	}
	
	public boolean doesElementExistInOtherPageStateWithLowerScrollOffset(Element element){
		return false;
	}

	public ElementState findById(long id) {
		return element_repo.findById(id).get();
	}

	public Set<Rule> getRules(String user_id, String element_key) {
		return element_repo.getRules(user_id, element_key);
	}

	public Set<Rule> addRuleToFormElement(String username, String element_key, Rule rule) {
		//Check that rule doesn't already exist
		Rule rule_record = element_repo.getElementRule(username, element_key, rule.getKey());
		if(rule_record == null) {
			rule_record = element_repo.addRuleToFormElement(username, element_key, rule.getKey());
			return element_repo.getRules(username, element_key);
		}
		else {
			throw new ExistingRuleException(rule.getType().toString());
		}
	}

	public ElementState findByOuterHtml(long account_id, String snippet) {
		return element_repo.findByOuterHtml(account_id, snippet);
	}

	public void clearBugMessages(long account_id, String form_key) {
		element_repo.clearBugMessages(account_id, form_key);
	}

	public List<ElementState> getChildElementsForUser(String user_id, String element_key) {
		return element_repo.getChildElementsForUser(user_id, element_key);
	}
	
	public List<ElementState> getChildElements(String page_key, String xpath) {
		assert page_key != null;
		assert !page_key.isEmpty();
		assert xpath != null;
		assert !xpath.isEmpty();
		
		List<ElementState> element_states = page_state_service.getElementStates(page_key);
		
		// get elements that are the the child of the element state
		List<ElementState> child_element_states = new ArrayList<>();
		for(ElementState element : element_states) {
			if(!element.getXpath().contentEquals(xpath) && element.getXpath().contains(xpath)) {
				child_element_states.add(element);
			}
		}
		
		return child_element_states;
	}
	
	public List<ElementState> getChildElementForParent(String parent_key, String child_element_key) {
		return element_repo.getChildElementForParent(parent_key, child_element_key);
	}

	@Deprecated
	public ElementState getParentElement(String user_id, Domain domain, String page_key, String element_state_key) {
		return element_repo.getParentElement(user_id, domain, page_key, element_state_key);
	}

	/**
	 * gets parent element for given {@link Element} within the given {@link PageState}
	 * 
	 * @param page_state_key
	 * @param element_state_key
	 * @return
	 */
	public ElementState getParentElement(String page_state_key, String element_state_key) {
		return element_repo.getParentElement(page_state_key, element_state_key);
	}
	
	public void addChildElement(String parent_element_key, String child_element_key) {
		//check if element has child already
		if(getChildElementForParent(parent_element_key, child_element_key).isEmpty()) {
			element_repo.addChildElement(parent_element_key, child_element_key);
		}
	}

	/**
	 * Fetch element that is the parent of the given child element for a given page
	 * 
	 * @param page_state_key
	 * @param child_key
	 * 
	 * @return
	 * 
	 * @pre page_state_key != null
	 * @pre child_key != null
	 */
	public ElementState findByPageStateAndChild(String page_state_key, String child_key) {
		assert page_state_key != null;
		assert child_key != null;
		return element_repo.findByPageStateAndChild(page_state_key, child_key);
	}

	public ElementState findByPageStateAndXpath(String page_state_key, String xpath) {
		assert page_state_key != null;
		assert xpath != null;
		return element_repo.findByPageStateAndXpath(page_state_key, xpath);
	}

	public List<ElementState> saveAll(List<ElementState> element_states, long page_state_id) {
		return element_states.parallelStream()
									   //.filter(f -> !existing_keys.contains(f.getKey()))
									   .map(element -> save(element))
									   .collect(Collectors.toList());

		//return element_repo.saveAll(element_states);
	}

	/**
	 * Returns subset of element keys that exist within the database 
	 * 
	 * @param element_keys
	 * @return
	 */
	public List<String> getAllExistingKeys(long page_state_id) {
		return element_repo.getAllExistingKeys(page_state_id);
	}

	public List<ElementState> getElements(Set<String> existing_keys) {
		return element_repo.getElements(existing_keys);
	}
}
