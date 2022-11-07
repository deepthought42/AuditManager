package com.looksee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.ElementState;
import com.looksee.models.journeys.LoginStep;
import com.looksee.models.journeys.SimpleStep;
import com.looksee.models.journeys.Step;
import com.looksee.models.repository.LoginStepRepository;
import com.looksee.models.repository.SimpleStepRepository;
import com.looksee.models.repository.StepRepository;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * Enables interacting with database for {@link SimpleStep Steps}
 */
@Service
@Retry(name = "neoforj")
public class StepService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StepService.class);

	@Autowired
	private StepRepository step_repo;
	
	@Autowired
	private SimpleStepRepository simple_step_repo;

	@Autowired
	private LoginStepRepository login_step_repo;
	
	public Step findByKey(String step_key) {
		return step_repo.findByKey(step_key);
	}

	public Step save(Step step) {
		assert step != null;
		
		if(step instanceof SimpleStep) {
			SimpleStep step_record = simple_step_repo.findByKey(step.getKey());
			
			if(step_record != null) {
				step_record.setElementState(simple_step_repo.getElementState(step.getKey()));
				step_record.setStartPage(simple_step_repo.getStartPage(step.getKey()));
				step_record.setEndPage(simple_step_repo.getEndPage(step.getKey()));
				return step_record;
			}
			
			SimpleStep simple_step = (SimpleStep)step;
			
			SimpleStep new_simple_step = new SimpleStep();
			new_simple_step.setAction(simple_step.getAction());
			new_simple_step.setActionInput(simple_step.getActionInput());
			new_simple_step.setKey(simple_step.generateKey());
			new_simple_step = simple_step_repo.save(new_simple_step);
			new_simple_step.setStartPage(simple_step_repo.addStartPage(new_simple_step.getId(), simple_step.getStartPage().getId()));
			new_simple_step.setEndPage(simple_step_repo.addEndPage(new_simple_step.getId(), simple_step.getEndPage().getId()));
			new_simple_step.setElementState(simple_step_repo.addElementState(new_simple_step.getId(), simple_step.getElementState().getId()));
			return new_simple_step;
		}
		else if(step instanceof LoginStep) {
			log.warn("looking up LOGIN step with key :: "+step.getKey());
			LoginStep step_record = login_step_repo.findByKey(step.getKey());
			if(step_record != null) {
				log.warn("found login step with key :: "+step_record.getKey());
				log.warn("loading LOGIN STEP connections...");
				step_record.setTestUser(login_step_repo.getTestUser(step_record.getId()));
				step_record.setUsernameElement(login_step_repo.getUsernameElement(step_record.getId()));
				step_record.setPasswordElement(login_step_repo.getPasswordElement(step_record.getId()));
				step_record.setSubmitElement(login_step_repo.getSubmitElement(step_record.getId()));
				step_record.setStartPage(login_step_repo.getStartPage(step_record.getId()));
				step_record.setEndPage(login_step_repo.getEndPage(step_record.getId()));

				return step_record;
			}
			
			LoginStep login_step = (LoginStep)step;
			
			LoginStep new_login_step = new LoginStep();
			new_login_step.setKey(login_step.generateKey());
			log.warn("saving login step");
			new_login_step = login_step_repo.save(new_login_step);
			log.warn("adding start page to login step");
			new_login_step.setStartPage(login_step_repo.addStartPage(new_login_step.getId(), login_step.getStartPage().getId()));
			
			log.warn("setting end page");
			new_login_step.setEndPage(login_step_repo.addEndPage(new_login_step.getId(), login_step.getEndPage().getId()));
			
			//ElementState username_input = element_state_service.findById(login_step.getUsernameElement().getId());
			log.warn("adding username element to login step");
			new_login_step.setUsernameElement(login_step_repo.addUsernameElement(new_login_step.getId(), login_step.getUsernameElement().getId()));
			
			//ElementState password_input = element_state_service.findById(login_step.getPasswordElement().getId());
			log.warn("adding password element to login step");
			new_login_step.setPasswordElement(login_step_repo.addPasswordElement(new_login_step.getId(), login_step.getPasswordElement().getId()));
			
			//ElementState submit_element = element_state_service.findById(login_step.getSubmitElement().getId());
			log.warn("adding submit element to login step");
			new_login_step.setSubmitElement(login_step_repo.addSubmitElement(new_login_step.getId(), login_step.getSubmitElement().getId()));
			
			//TestUser user = test_user_service.findById(login_step.getTestUser().getId());
			log.warn("login step test user id :: "+login_step.getTestUser().getId());
			new_login_step.setTestUser(login_step_repo.addTestUser(new_login_step.getId(), login_step.getTestUser().getId()));
			
			return new_login_step;
		}
		
		return null;
	}

	public ElementState getElementState(String step_key) {
		return step_repo.getElementState(step_key);
	}
}
