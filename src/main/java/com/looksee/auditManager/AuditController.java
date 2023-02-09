package com.looksee.auditManager;

/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// [START cloudrun_pubsub_handler]
// [START run_pubsub_handler]
import java.util.Base64;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.looksee.auditManager.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.auditManager.mapper.Body;
import com.looksee.auditManager.models.AuditRecord;
import com.looksee.auditManager.models.enums.ExecutionStatus;
import com.looksee.auditManager.models.message.PageAuditMessage;
import com.looksee.auditManager.models.message.PageBuiltMessage;
import com.looksee.auditManager.services.AccountService;
import com.looksee.auditManager.services.AuditRecordService;
import com.looksee.auditManager.models.PageAuditRecord;

// PubsubController consumes a Pub/Sub message.
@RestController
public class AuditController {
	private static Logger log = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private PubSubPageAuditPublisherImpl audit_record_topic;
	
	@Autowired
	private AccountService account_service;
	
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> receiveMessage(@RequestBody Body body) throws JsonMappingException, JsonProcessingException, ExecutionException, InterruptedException {
		Body.Message message = body.getMessage();
		String data = message.getData();
	    String target = !data.isEmpty() ? new String(Base64.getDecoder().decode(data)) : "";
        log.warn("page-built msg received = "+target);

	    ObjectMapper input_mapper = new ObjectMapper();
	    PageBuiltMessage page_created_msg = input_mapper.readValue(target, PageBuiltMessage.class);
	    
	    JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
	    
		
		//if page has already been audited then return success with appropriate message
		//otherwise add page to page audit record and and publish page audit message to pubsub
		if(wasPageAlreadyAudited(page_created_msg.getDomainAuditRecordId(), page_created_msg.getPageId())) {
			return new ResponseEntity<String>("Page "+page_created_msg.getPageId()+" was already audited", HttpStatus.OK);
		}
		else {
			AuditRecord audit_record = new PageAuditRecord(ExecutionStatus.BUILDING_PAGE, new HashSet<>(), true);
			audit_record = audit_record_service.save(audit_record);
			audit_record_service.addPageToAuditRecord(audit_record.getId(), 
													  page_created_msg.getPageId());
			if(page_created_msg.getDomainAuditRecordId() < 0) {
				account_service.addAuditRecord(page_created_msg.getAccountId(), 
											   audit_record.getId());
			}
			else {				
				//add page to domain audit record. This is how things work at the moment, so propagating forward.
				// DO NOT DELETE UNLESS YOU HAVE UPDATED ALL LOGIC THAT THIS IMPACTS FOR AUDITS AND JOURNEY EXPANSION
				//TODO : replace need for page to be directly associated with DomainAuditRecord for stats on front end
				audit_record_service.addPageToAuditRecord(page_created_msg.getDomainAuditRecordId(), 
														  page_created_msg.getPageId());
				
				audit_record_service.addPageAuditToDomainAudit(page_created_msg.getDomainAuditRecordId(),
																audit_record.getId());
			}
			
			//send message to page audit message topic
			PageAuditMessage audit_msg = new PageAuditMessage(	page_created_msg.getAccountId(),
																page_created_msg.getDomainId(),
																page_created_msg.getDomainAuditRecordId(),
																audit_record.getId(),
																page_created_msg.getPageId());
			
			String audit_record_json = mapper.writeValueAsString(audit_msg);
			log.warn("Sending PageAuditMessage to Pub/Sub = "+audit_record_json);
			audit_record_topic.publish(audit_record_json);
		}
		
		
		//update audit record
		//TODO : MOVE FOLLOWING LOGIC TO CLOUD RUN TO HANDLE AUDIT UPDATES
		/*
		AuditRecord audit_record = audit_record_service.findById(page_created_msg.getPageAuditRecordId()).get();
		
		Audit audit = page_created_msg.getAudit();
		
		audit = audit_service.save(audit);
		audit_record_service.addAudit( audit_record.getId(), audit.getId() );
		
		if(AuditCategory.AESTHETICS.equals(audit.getCategory())) {
			audit_record.setAestheticAuditProgress(audit_msg.getAuditProgress());
		}
		if(AuditCategory.CONTENT.equals(audit.getCategory())) {
			audit_record.setContentAuditProgress(audit_msg.getAuditProgress());
		}
		if(AuditCategory.INFORMATION_ARCHITECTURE.equals(audit.getCategory())) {
			audit_record.setInfoArchitectureAuditProgress(audit_msg.getAuditProgress());
		}
		
		audit_record_service.save(audit_record);
		
		boolean is_page_audit_complete = AuditUtils.isPageAuditComplete(audit_record);						
		if(is_page_audit_complete) {
			audit_record.setEndTime(LocalDateTime.now());
			audit_record.setStatus(ExecutionStatus.COMPLETE);
			audit_record = audit_record_service.save(audit_record);	
		
			PageState page = audit_record_service.getPageStateForAuditRecord(audit_record.getId());								
			Account account = account_service.findById(audit_msg.getAccountId()).get();
			
			log.warn("sending email to account :: "+account.getEmail());
			mail_service.sendPageAuditCompleteEmail(account.getEmail(), page.getUrl(), audit_record.getId());
		}*/
		
		return new ResponseEntity<String>("Successfully sent message to audit manager", HttpStatus.OK);
  }

	private boolean wasPageAlreadyAudited(long domain_audit_id, long page_id) {
		AuditRecord record = audit_record_service.findPageWithId(domain_audit_id, page_id);
		return record != null;
	}
	
  /*
  public void publishMessage(String messageId, Map<String, String> attributeMap, String message) throws ExecutionException, InterruptedException {
      log.info("Sending Message to the topic:::");
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
              .putAllAttributes(attributeMap)
              .setData(ByteString.copyFromUtf8(message))
              .setMessageId(messageId)
              .build();

      pubSubPublisherImpl.publish(pubsubMessage);
  }
  */
}
// [END run_pubsub_handler]
// [END cloudrun_pubsub_handler]