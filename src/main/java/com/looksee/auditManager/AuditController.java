package com.looksee.auditManager;

import java.util.ArrayList;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.looksee.auditManager.models.Audit;
import com.looksee.auditManager.models.AuditRecord;
import com.looksee.auditManager.models.DomainAuditRecord;
import com.looksee.auditManager.models.enums.AuditCategory;
import com.looksee.auditManager.models.enums.AuditName;
import com.looksee.auditManager.models.enums.ExecutionStatus;
import com.looksee.auditManager.models.message.PageAuditMessage;
import com.looksee.auditManager.models.message.SinglePageBuiltMessage;
import com.looksee.auditManager.models.message.DomainPageBuiltMessage;
import com.looksee.auditManager.services.AuditRecordService;
import com.looksee.auditManager.services.AuditService;
import com.looksee.auditManager.services.MessageBroadcaster;
import com.looksee.auditManager.services.PageStateService;
import com.looksee.auditManager.models.dto.PageAuditDto;
import com.looksee.utils.AuditUtils;
import com.looksee.auditManager.models.PageAuditRecord;
import com.looksee.auditManager.models.PageState;

// PubsubController consumes a Pub/Sub message.
@RestController
public class AuditController {
	private static Logger log = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private PubSubPageAuditPublisherImpl audit_record_topic;
	
	@Autowired
	private PageStateService page_state_service;
	
	@Autowired
	private AuditService audit_service;
	
	@Autowired
	private MessageBroadcaster pusher;
	
	/**
	 * 
	 * @param body
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> receiveMessage(@RequestBody Body body) throws JsonMappingException, JsonProcessingException, ExecutionException, InterruptedException {
		Body.Message message = body.getMessage();
		String data = message.getData();
	    String target = !data.isEmpty() ? new String(Base64.getDecoder().decode(data)) : "";
        log.warn("page-built msg received = " + target);

	    ObjectMapper input_mapper = new ObjectMapper();
	    JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

	    try {
	    	DomainPageBuiltMessage domain_audit_message = input_mapper.readValue(target, DomainPageBuiltMessage.class);
	    	//if page has already been audited then return success with appropriate message
			//otherwise add page to page audit record and and publish page audit message to pubsub
	    	List<AuditName> audit_list = new ArrayList<>();
			DomainAuditRecord record = null;
			
			Optional<AuditRecord> opt_record = audit_record_service.findById(domain_audit_message.getDomainAuditRecordId());
			if(opt_record.isPresent()) {
				log.warn("looking up domain audit record with id = "+domain_audit_message.getDomainAuditRecordId());
				record = (DomainAuditRecord)opt_record.get();
				audit_list = record.getAuditLabels();
			}
			else {
				audit_list = new ArrayList<>();
				//VISUAL DESIGN AUDIT
				audit_list.add(AuditName.TEXT_BACKGROUND_CONTRAST);
				audit_list.add(AuditName.NON_TEXT_BACKGROUND_CONTRAST);
				
				//INFO ARCHITECTURE AUDITS
				audit_list.add(AuditName.LINKS);
				audit_list.add(AuditName.TITLES);
				audit_list.add(AuditName.ENCRYPTED);
				audit_list.add(AuditName.METADATA);
				
				//CONTENT AUDIT
				audit_list.add(AuditName.ALT_TEXT);
				audit_list.add(AuditName.READING_COMPLEXITY);
				audit_list.add(AuditName.PARAGRAPHING);
				audit_list.add(AuditName.IMAGE_COPYRIGHT);
				audit_list.add(AuditName.IMAGE_POLICY);
			}
				
			if(!wasPageAlreadyCataloged(domain_audit_message.getDomainAuditRecordId(), domain_audit_message.getPageId())) {
				//add page to domain audit record. This is how things work at the moment, so propagating forward.
				// DO NOT DELETE UNLESS YOU HAVE UPDATED ALL LOGIC THAT THIS IMPACTS FOR AUDITS AND JOURNEY EXPANSION
				//TODO : replace need for page to be directly associated with DomainAuditRecord for stats on front end
				log.warn("adding page to domain audit record "+domain_audit_message.getDomainAuditRecordId());
				audit_record_service.addPageToAuditRecord(domain_audit_message.getDomainAuditRecordId(), 
															domain_audit_message.getPageId());
			}
			
			boolean already_audited = audit_record_service.wasPageAlreadyAudited(domain_audit_message.getDomainAuditRecordId(), domain_audit_message.getPageId());
			boolean is_landable = page_state_service.isPageLandable(domain_audit_message.getPageId());
			log.warn("page with id = "+domain_audit_message.getPageId()+" already audited? = "+already_audited);
			log.warn("is landable? = "+is_landable);
			
			if( !audit_record_service.wasPageAlreadyAudited(domain_audit_message.getDomainAuditRecordId(), domain_audit_message.getPageId())
					&& page_state_service.isPageLandable(domain_audit_message.getPageId())) {
				log.warn("Creating PageAuditRecord");
				AuditRecord audit_record = new PageAuditRecord(ExecutionStatus.BUILDING_PAGE,
																new HashSet<>(),
																null,
																true, 
																audit_list);
				
				audit_record = audit_record_service.save(audit_record);
				log.warn("adding page audit to domain audit "+audit_record.getId());
				audit_record_service.addPageAuditToDomainAudit(domain_audit_message.getDomainAuditRecordId(),
																audit_record.getId());
				
				audit_record_service.addPageToAuditRecord(  audit_record.getId(),
															domain_audit_message.getPageId());
				
				//send message to page audit message topic
				PageAuditMessage audit_msg = new PageAuditMessage(	domain_audit_message.getAccountId(),
																	audit_record.getId());
				log.warn("sending page audit message = "+audit_msg.getPageAuditId());
				String audit_record_json = mapper.writeValueAsString(audit_msg);
				log.warn("(DomainAudit) Sending PageAuditMessage to Pub/Sub = "+audit_record_json);
				audit_record_topic.publish(audit_record_json);
			}
			else {
				log.warn("Page with id = "+domain_audit_message.getPageId()+" has already been sent to be audited");
			}
	    }
	    catch(Exception e){
	    	
	    }
	    
	    /****************
	     * 
	     * Processes a Single Page Built Message, which indicates that a single page audit is being performed
	     * 
	     **************/
	    try {
	    	log.warn("Received single page built message");
		    SinglePageBuiltMessage page_created_msg = input_mapper.readValue(target, SinglePageBuiltMessage.class);
		    
			if(page_created_msg.getPageAuditId() >= 0) {
		    	//add page state to page audit record
		    	audit_record_service.addPageToAuditRecord(page_created_msg.getPageAuditId(), 
						  								  page_created_msg.getPageId());
		    	
		    	//send message to audit record topic to have page audited
		    	//send message to page audit message topic
				PageAuditMessage audit_msg = new PageAuditMessage(	page_created_msg.getAccountId(),
																	page_created_msg.getPageAuditId());
				
				String page_audit_msg_json = mapper.writeValueAsString(audit_msg);
				log.warn("(SinglePageAudit) Sending PageAuditMessage to Pub/Sub = "+page_audit_msg_json);
				audit_record_topic.publish(page_audit_msg_json);
				
				PageState page = page_state_service.findById(page_created_msg.getPageId()).get();
				//PageAuditRecord audit_record = (PageAuditRecord)audit_record_service.findById(page_created_msg.getPageAuditId()).get();
				PageAuditDto audit_dto = builPagedAuditdDto(page_created_msg.getPageAuditId(), page.getUrl());
				pusher.sendAuditUpdate(Long.toString( page_created_msg.getAccountId() ), audit_dto);
		    }
	    }catch(Exception e) {
	    	e.printStackTrace();
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

	/**
	 * Creates an {@linkplain PageAuditDto} using page audit ID and the provided page_url
	 * @param pageAuditId
	 * @param page_url
	 * @return
	 */
	private PageAuditDto builPagedAuditdDto(long pageAuditId, String page_url) {
		//get all audits
		Set<Audit> audits = audit_record_service.getAllAudits(pageAuditId);
		Set<AuditName> audit_labels = new HashSet<AuditName>();
		audit_labels.add(AuditName.TEXT_BACKGROUND_CONTRAST);
		audit_labels.add(AuditName.NON_TEXT_BACKGROUND_CONTRAST);
		audit_labels.add(AuditName.TITLES);
		audit_labels.add(AuditName.IMAGE_COPYRIGHT);
		audit_labels.add(AuditName.IMAGE_POLICY);
		audit_labels.add(AuditName.LINKS);
		audit_labels.add(AuditName.ALT_TEXT);
		audit_labels.add(AuditName.METADATA);
		audit_labels.add(AuditName.READING_COMPLEXITY);
		audit_labels.add(AuditName.PARAGRAPHING);
		audit_labels.add(AuditName.ENCRYPTED);
		//count audits for each category
		//calculate content score
		//calculate aesthetics score
		//calculate information architecture score
		double visual_design_progress = AuditUtils.calculateProgress(AuditCategory.AESTHETICS, 
																 1, 
																 audits, 
																 AuditUtils.getAuditLabels(AuditCategory.AESTHETICS, audit_labels));
		double content_progress = AuditUtils.calculateProgress(AuditCategory.CONTENT, 
																1, 
																audits, 
																audit_labels);
		double info_architecture_progress = AuditUtils.calculateProgress(AuditCategory.INFORMATION_ARCHITECTURE, 
																		1, 
																		audits, 
																		audit_labels);

		double content_score = AuditUtils.calculateScoreByCategory(audits, AuditCategory.CONTENT);
		double info_architecture_score = AuditUtils.calculateScoreByCategory(audits, AuditCategory.INFORMATION_ARCHITECTURE);
		double visual_design_score = AuditUtils.calculateScoreByCategory(audits, AuditCategory.AESTHETICS);
		double a11y_score = AuditUtils.calculateScoreByCategory(audits, AuditCategory.ACCESSIBILITY);

		double data_extraction_progress = 1;
		String message = "";
		ExecutionStatus execution_status = ExecutionStatus.UNKNOWN;
		if(visual_design_progress < 1 || content_progress < 1 || visual_design_progress < 1) {
			execution_status = ExecutionStatus.IN_PROGRESS;
		}
		else {
			execution_status = ExecutionStatus.COMPLETE;
		}
		
		return new PageAuditDto(pageAuditId, 
								page_url, 
								content_score, 
								content_progress, 
								info_architecture_score, 
								info_architecture_progress, 
								a11y_score,
								visual_design_score,
								visual_design_progress,
								data_extraction_progress, 
								message, 
								execution_status);
	}

	private boolean wasPageAlreadyCataloged(long domain_audit_id, long page_id) {
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