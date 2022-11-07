package com.looksee.models.message;

import com.looksee.models.Form;
import com.looksee.models.PageState;

import akka.actor.ActorRef;

/**
 * Message that transports {@link Form} and {@link PageState}
 */
public class FormDiscoveredMessage extends Message {

	private Form form;
	private PageState page;
	private ActorRef domain_actor;
	private ActorRef discovery_actor;

	
	public FormDiscoveredMessage(Form form, PageState page, long account_id, long domain_id) {
		setForm(form);
		setPage(page);
		setDomainId(domain_id);
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

	public PageState getPage() {
		return page;
	}

	public void setPage(PageState page) {
		this.page = page;
	}
}
