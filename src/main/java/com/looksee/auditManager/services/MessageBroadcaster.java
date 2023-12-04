package com.looksee.auditManager.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.looksee.auditManager.models.dto.AuditUpdateDto;
import com.pusher.rest.Pusher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Defines methods for emitting data to subscribed clients
 */
@Service
public class MessageBroadcaster {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(MessageBroadcaster.class);
	
	private Pusher pusher;
	
	public MessageBroadcaster(@Value( "${pusher.appId}" ) String app_id,
			@Value( "${pusher.key}" ) String key,
			@Value( "${pusher.secret}" ) String secret,
			@Value("${pusher.cluster}") String cluster) {
		pusher = new Pusher(app_id, key, secret);
		pusher.setCluster(cluster);
		pusher.setEncrypted(true);
	}
	
	/**
	 * Sends {@linkplain DomainAuditDto} to user via Pusher
	 * @param channel_id
	 * @param domain_audit_update
	 * 
	 * @throws JsonProcessingException
	 * 
	 * @pre channel_id != null
	 * @pre !channel_id.isEmpty()
	 * @pre domain_audit_update != null
	 */
	public void sendAuditUpdate(String channel_id, AuditUpdateDto domain_audit_update) throws JsonProcessingException {
		assert channel_id != null;
		assert !channel_id.isEmpty();
		assert domain_audit_update != null;
		
		log.warn("Sending page audit record to user");
		ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String audit_record_json = mapper.writeValueAsString(domain_audit_update);
		pusher.trigger(channel_id, "audit-record", audit_record_json);
	}
}
