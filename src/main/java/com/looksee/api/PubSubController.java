package com.looksee.api;

import static com.looksee.config.SpringExtension.SpringExtProvider;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.looksee.models.audit.AuditRecord;
import com.looksee.models.message.PageDataExtractionMessage;
import com.looksee.services.AuditRecordService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

// PubsubController consumes a Pub/Sub message.
@Controller
public class PubSubController {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PubSubController.class.getName());
	
	@Autowired
	private ActorSystem actor_system;

    @Autowired
    protected AuditRecordService audit_record_service;
	
	/**
	 * Sends request body to {@link AuditManager} actor to run audits for the given {@link PageState}
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity receiveMessage(@RequestBody PageDataExtractionMessage message) {
	    // Get PubSub message from request body.
	    //Body.Message message = body.getMessage();
	    if (message == null) {
	      String msg = "Bad Request: invalid Pub/Sub message format";
	      System.out.println(msg);
	      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
	    }
	
	    //retrieve audit record and determine type of audit record
	    AuditRecord audit_record = audit_record_service.findById(message.getAuditRecordId()).get();
	    if(audit_record == null) {
	    	//TODO: SEND PUB SUB MESSAGE THAT AUDIT RECORD NOT FOUND WITH PAGE DATA EXTRACTION MESSAGE
	    }
	    else {
			log.warn("Initiating page audit = "+audit_record.getId());
			ActorRef audit_manager = actor_system.actorOf(SpringExtProvider.get(actor_system)
		   												.props("singlePageAuditManager"), "singlePageAuditManager"+UUID.randomUUID());
			audit_manager.tell(message, ActorRef.noSender());
	    }
	    
	    return new ResponseEntity("Successfully sent message to audit manager", HttpStatus.OK);
    }
}