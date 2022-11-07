package com.looksee.models.message;

import java.util.ArrayList;
import java.util.List;

import com.looksee.models.LookseeObject;
import com.looksee.models.enums.BrowserType;
import com.looksee.models.enums.PathStatus;

import akka.actor.ActorRef;

public class PathMessageOLD extends Message {

	private List<String> keys;
	private List<LookseeObject> path_objects;
	private ActorRef discovery_actor;	
	private ActorRef domain_actor;
	private PathStatus status;
	private BrowserType browser;
	
	public PathMessageOLD(List<String> keys, 
					   List<LookseeObject> path_objects, 
					   ActorRef discovery_actor, 
					   PathStatus status, 
					   BrowserType browser, 
					   ActorRef domain_actor, 
					   long domain_id, 
					   long account_id){
		setKeys(keys);
		setPathObjects(path_objects);
		setDiscoveryActor(discovery_actor);
		setStatus(status);
		setBrowser(browser);
		setDomainActor(domain_actor);
		setDomainId(domain_id);
		setAccountId(account_id);
	}

	public List<String> getKeys() {
		return keys;
	}
	
	private void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public List<LookseeObject> getPathObjects() {
		return path_objects;
	}
	
	private void setPathObjects(List<LookseeObject> path_objects) {
		this.path_objects = path_objects;
	}
	
	public PathMessageOLD clone(){
		return new PathMessageOLD(new ArrayList<>(keys), new ArrayList<>(path_objects), getDiscoveryActor(), getStatus(), getBrowser(), getDomainActor(), getDomainId(), getAccountId());
	}

	public ActorRef getDiscoveryActor() {
		return discovery_actor;
	}

	private void setDiscoveryActor(ActorRef discovery_actor) {
		this.discovery_actor = discovery_actor;
	}

	public PathStatus getStatus() {
		return status;
	}

	private void setStatus(PathStatus status) {
		this.status = status;
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
