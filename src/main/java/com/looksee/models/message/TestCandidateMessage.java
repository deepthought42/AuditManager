package com.looksee.models.message;

import java.util.List;

import com.looksee.models.LookseeObject;
import com.looksee.models.PageState;
import com.looksee.models.enums.BrowserType;

import akka.actor.ActorRef;

/**
 * 
 * 
 */
public class TestCandidateMessage extends Message {

	private List<String> keys;
	private List<LookseeObject> path_objects;
	private ActorRef discovery_actor;
	private ActorRef domain_actor;
	private PageState result_page;
	private BrowserType browser;
	
	public TestCandidateMessage(
			List<String> keys, 
			List<LookseeObject> path_objects, 
			ActorRef discovery_actor, 
			PageState result_page, 
			BrowserType browser, 
			ActorRef domain_actor, 
			long domain_id, 
			long account_id
	){
		setDiscoveryActor(discovery_actor);
		setBrowser(browser);
		this.keys = keys;
		this.path_objects = path_objects;
		this.result_page = result_page;
		setDomainActor(domain_actor);
		setDomainId(domain_id);
		setAccountId(account_id);
	}

	public List<String> getKeys() {
		return keys;
	}

	public List<LookseeObject> getPathObjects() {
		return path_objects;
	}

	public PageState getResultPage() {
		return result_page;
	}

	public ActorRef getDiscoveryActor() {
		return discovery_actor;
	}

	public void setDiscoveryActor(ActorRef discovery_actor) {
		this.discovery_actor = discovery_actor;
	}

	public BrowserType getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public ActorRef getDomainActor() {
		return domain_actor;
	}

	public void setDomainActor(ActorRef domain_actor) {
		this.domain_actor = domain_actor;
	}
}
