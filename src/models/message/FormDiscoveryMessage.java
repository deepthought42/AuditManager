package com.looksee.models.message;

import com.looksee.models.DiscoveryRecord;
import com.looksee.models.Domain;
import com.looksee.models.Form;
import com.looksee.models.PageState;

import akka.actor.ActorRef;

/**
 * Message that transports {@link Form}, {@link DiscoveryRecord}, {@link PageState}, and {@link Domain} objects
 */
public class FormDiscoveryMessage extends Message{

	private Form form;
	private DiscoveryRecord discovery;
	private PageState page;
	private ActorRef domain_actor;
	private ActorRef discovery_actor;
	
	public FormDiscoveryMessage(Form form, DiscoveryRecord discovery, long domain_id, PageState page, long account_id) {
		setForm(form);
		setDiscovery(discovery);
		setDomainId(domain_id);
		setPage(page);
		setAccountId(account_id);
	}

	public Form getForm() {
		return form;
	}

	private void setForm(Form form) {
		this.form = form;
	}

	public void setDomainActor(ActorRef domain_actor) {
		this.domain_actor = domain_actor;
	}

	public ActorRef getDomainActor() {
		return domain_actor;
	}

	public ActorRef getDiscoveryActor() {
		return discovery_actor;
	}

	public void setDiscoveryActor(ActorRef discovery_actor) {
		this.discovery_actor = discovery_actor;
	}

	public DiscoveryRecord getDiscovery() {
		return discovery;
	}

	public void setDiscovery(DiscoveryRecord discovery) {
		this.discovery = discovery;
	}

	public PageState getPage() {
		return page;
	}

	public void setPage(PageState page) {
		this.page = page;
	}
}
