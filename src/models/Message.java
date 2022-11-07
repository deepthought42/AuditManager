package com.looksee.models;

/**
 * A data wrapper for messages to be passed around between actors. This wrapper includes in the account
 * key for a request alongside data so that actors can keep track of who they are performing work for.
 *
 * @param <T> data object that is being passed inside of message
 */
public class Message<T> {
	private final String account_key;
	private final T datum;
	private final DiscoveryRecord discovery;
	
	public Message(String account_key, T data, DiscoveryRecord discovery){
		this.account_key = account_key;
		this.datum = data;
		this.discovery = discovery;
	}
	
	public String getAccountKey(){
		return this.account_key;
	}
	
	public T getData(){
		return this.datum;
	}

	public DiscoveryRecord getDiscovery() {
		return discovery;
	}	
}
