package com.looksee.models.message;

import com.looksee.models.Test;
import com.looksee.models.enums.BrowserType;

import akka.actor.ActorRef;

public class TestMessage extends Message{

	private Test test;
	private ActorRef discovery_actor;	
	private ActorRef domain_actor;
	private BrowserType browser;
	
	public TestMessage(Test test, 
					   ActorRef discovery_actor, 
					   BrowserType browser, 
					   ActorRef domain_actor, 
					   long domain_id, 
					   long account_id
    ){
		setTest(test);
		setDiscoveryActor(discovery_actor);
		setBrowser(browser);
		setDomainActor(domain_actor);
		setDomainId(domain_id);
		setAccountId(account_id);
	}

	public TestMessage clone(){
		return new TestMessage(test, getDiscoveryActor(), getBrowser(), getDomainActor(), getDomainId(), getAccountId());
	}

	public ActorRef getDiscoveryActor() {
		return discovery_actor;
	}

	private void setDiscoveryActor(ActorRef discovery_actor) {
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

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}
}
