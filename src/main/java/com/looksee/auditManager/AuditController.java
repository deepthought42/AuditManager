package com.looksee.auditManager;

import java.util.Base64;
import java.util.HashSet;
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
import com.looksee.auditManager.models.AuditRecord;
import com.looksee.auditManager.models.DomainAuditRecord;
import com.looksee.auditManager.models.PageAuditRecord;
import com.looksee.auditManager.models.PageState;
import com.looksee.auditManager.models.enums.AuditName;
import com.looksee.auditManager.models.enums.ExecutionStatus;
import com.looksee.auditManager.models.message.DomainPageBuiltMessage;
import com.looksee.auditManager.models.message.PageAuditMessage;
import com.looksee.auditManager.models.message.PageBuiltMessage;
import com.looksee.auditManager.models.message.SinglePageBuiltMessage;
import com.looksee.auditManager.services.AuditRecordService;
import com.looksee.auditManager.services.PageStateService;

/**
 * Main ReST Controller for this micro-service. Expects to receive either a {@linkplain DomainPageBuiltMessage} or a {@linkplain SinglePageBuiltMessage}
 *   and passes on information appropriately to micro-services that perform audits as well as sending an {@linkPlain AuditUpdateDto} message to the audit update topic
 */
@RestController
public class AuditController {
	private static Logger log = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	private AuditRecordService audit_record_service;
	
	@Autowired
	private PubSubPageAuditPublisherImpl audit_record_topic;
	
	@Autowired
	private PageStateService page_state_service;
	
	/**
	 * 
	 * @param body
	 * @return
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> receiveMessage(@RequestBody Body body) 
		throws JsonMappingException, JsonProcessingException, ExecutionException, InterruptedException 
	{
		Body.Message message = body.getMessage();
		String data = message.getData();
		String target = !data.isEmpty() ? new String(Base64.getDecoder().decode(data)) : "";
        log.warn("page-built msg received = " + target);

		ObjectMapper input_mapper = new ObjectMapper();
		JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

		try {
			PageBuiltMessage page_built_message = input_mapper.readValue(target, PageBuiltMessage.class);
	    	//DomainPageBuiltMessage domain_audit_message = input_mapper.readValue(target, DomainPageBuiltMessage.class);
	    	//if page has already been audited then return success with appropriate message
			//otherwise add page to page audit record and and publish page audit message to pubsub
			Set<AuditName> auditNames = new HashSet<>();
			DomainAuditRecord record = null;
			
			Optional<AuditRecord> opt_record = audit_record_service.findById(page_built_message.getAuditRecordId());
			if(opt_record.isPresent()) {
				log.warn("looking up domain audit record with id = "+page_built_message.getAuditRecordId());
				record = (DomainAuditRecord)opt_record.get();
				auditNames = record.getAuditLabels();
			}
			else {
				//VISUAL DESIGN AUDIT
				auditNames.add(AuditName.TEXT_BACKGROUND_CONTRAST);
				auditNames.add(AuditName.NON_TEXT_BACKGROUND_CONTRAST);
				
				//INFO ARCHITECTURE AUDITS
				auditNames.add(AuditName.LINKS);
				auditNames.add(AuditName.TITLES);
				auditNames.add(AuditName.ENCRYPTED);
				auditNames.add(AuditName.METADATA);
				
				//CONTENT AUDIT
				auditNames.add(AuditName.ALT_TEXT);
				auditNames.add(AuditName.READING_COMPLEXITY);
				auditNames.add(AuditName.PARAGRAPHING);
				auditNames.add(AuditName.IMAGE_COPYRIGHT);
				auditNames.add(AuditName.IMAGE_POLICY);
			}
			
			boolean already_audited = audit_record_service.wasPageAlreadyAudited(page_built_message.getAuditRecordId(), page_built_message.getPageId());
			boolean is_landable = page_state_service.isPageLandable(page_built_message.getPageId());
			Optional<PageState> page_state = page_state_service.findById(page_built_message.getPageId());
			String url = "";
			if(page_state.isPresent()){
				url = page_state.get().getUrl();
				log.warn("Received page with url "+url);
			}
			log.warn("page with id = "+page_built_message.getPageId()+" already audited? = "+already_audited+";   is landable? = "+is_landable);
			
			if( !already_audited && is_landable) {
				log.warn("Creating PageAuditRecord");
				AuditRecord audit_record = new PageAuditRecord(ExecutionStatus.BUILDING_PAGE,
																new HashSet<>(),
																null,
																true,
																auditNames,
																url);
				
				audit_record = audit_record_service.save(audit_record);
				audit_record_service.addPageAuditToDomainAudit(page_built_message.getAuditRecordId(),
																audit_record.getId());
				
				audit_record_service.addPageToAuditRecord(  audit_record.getId(),
															page_built_message.getPageId());
				
				//send message to page audit message topic
				PageAuditMessage audit_msg = new PageAuditMessage(	page_built_message.getAccountId(),
																	audit_record.getId());
				String audit_record_json = mapper.writeValueAsString(audit_msg);
				log.warn("(DomainAudit) Sending PageAuditMessage to Pub/Sub = "+audit_record_json);
				audit_record_topic.publish(audit_record_json);
			}
			else {
				log.warn("Page with id = "+page_built_message.getPageId()+" has already been sent to be audited");
			}
			return new ResponseEntity<String>("Successfully sent message to audit manager", HttpStatus.OK);
		}
		catch(Exception e){
			log.error("Error occurred while mapping to DomainPageBuiltMessage");
			log.error("message : "+target);
			e.printStackTrace();
		}

		/****************
	     * 
	     * Processes a Single Page Built Message, which indicates that a single page audit is being performed
	     * 
	     **************/
		/*
	    try {
	    	log.warn("Received single page built message");
		    SinglePageBuiltMessage page_created_msg = input_mapper.readValue(target, SinglePageBuiltMessage.class);
		    
			if(page_created_msg.getPageAuditId() >= 0 && !audit_record_service.wasSinglePageAlreadyAudited(page_created_msg.getPageAuditId(), page_created_msg.getPageId())) {
		    	//add page state to page audit record
		    	audit_record_service.addPageToAuditRecord(page_created_msg.getPageAuditId(), 
						  								  page_created_msg.getPageId());
		    	
		    	//send message to audit record topic to have page audited
				PageAuditMessage audit_msg = new PageAuditMessage(	page_created_msg.getAccountId(),
																	page_created_msg.getPageAuditId());
				
				String page_audit_msg_json = mapper.writeValueAsString(audit_msg);
				log.warn("(SinglePageAudit) Sending PageAuditMessage to Pub/Sub = "+page_audit_msg_json);
				audit_record_topic.publish(page_audit_msg_json);
				
		    	//send message to page audit message topic
				AuditProgressUpdate audit_update = new AuditProgressUpdate(audit_msg.getAccountId(),
																			audit_msg.getPageAuditId(),
																			"Data extraction complete!");
				
				String audit_update_json = mapper.writeValueAsString(audit_update);
				audit_update_topic.publish(audit_update_json);

				//TODO: Replace following logic with a message that is publishes an update message to the audit-update topic
				//AuditUpdateDto audit_dto = buildAuditUpdatedDto(page_created_msg.getPageAuditId(), AuditLevel.PAGE);
				//pusher.sendAuditUpdate(Long.toString( page_created_msg.getPageAuditId() ), audit_dto);
		    }
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		*/
		
		return new ResponseEntity<String>("Successfully sent message to audit manager", HttpStatus.OK);
  }
}