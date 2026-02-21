package com.looksee.auditManager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.mapper.Body;
import com.looksee.models.PageState;
import com.looksee.models.audit.AuditRecord;
import com.looksee.models.audit.DomainAuditRecord;
import com.looksee.models.audit.PageAuditRecord;
import com.looksee.models.enums.AuditName;
import com.looksee.models.enums.ExecutionStatus;
import com.looksee.models.message.PageAuditMessage;
import com.looksee.models.message.PageBuiltMessage;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

/**
 * Main ReST Controller for this micro-service.
 */
@RestController
public class AuditController {
	private static final Logger log = LoggerFactory.getLogger(AuditController.class);

	private static final ObjectMapper INPUT_MAPPER = new ObjectMapper();
	private static final JsonMapper OUTPUT_MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

	private final AuditRecordService auditRecordService;
	private final PubSubPageAuditPublisherImpl auditRecordTopic;
	private final PageStateService pageStateService;

	public AuditController(
		AuditRecordService auditRecordService,
		PubSubPageAuditPublisherImpl auditRecordTopic,
		PageStateService pageStateService) {
		this.auditRecordService = auditRecordService;
		this.auditRecordTopic = auditRecordTopic;
		this.pageStateService = pageStateService;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> receiveMessage(@RequestBody Body body) {
		if (!hasValidPayload(body)) {
			log.warn("Received invalid Pub/Sub payload: message or data is missing");
			return badRequest("Invalid Pub/Sub payload");
		}

		String payload = decodePayload(body.getMessage().getData());
		if (payload == null) {
			return badRequest("Invalid message encoding");
		}

		PageBuiltMessage pageBuiltMessage = parseMessage(payload);
		if (pageBuiltMessage == null) {
			return badRequest("Invalid message format");
		}

		return processMessage(pageBuiltMessage);
	}

	private ResponseEntity<String> processMessage(PageBuiltMessage pageBuiltMessage) {
		try {
			Set<AuditName> auditNames = buildAuditNames(pageBuiltMessage.getAuditRecordId());

			boolean alreadyAudited = auditRecordService.wasPageAlreadyAudited(pageBuiltMessage.getAuditRecordId(), pageBuiltMessage.getPageId());
			boolean isLandable = pageStateService.isPageLandable(pageBuiltMessage.getPageId());
			Optional<PageState> pageState = pageStateService.findById(pageBuiltMessage.getPageId());

			if (!alreadyAudited && isLandable && pageState.isPresent()) {
				return createAndPublishAudit(pageBuiltMessage, pageState.get(), auditNames);
			}

			log.info("Skipping pageId={} (alreadyAudited={}, landable={}, pageStatePresent={})",
				pageBuiltMessage.getPageId(), alreadyAudited, isLandable, pageState.isPresent());
			return ResponseEntity.ok("Successfully processed message");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Audit processing interrupted", e);
			return new ResponseEntity<String>("Audit processing interrupted", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ExecutionException e) {
			log.error("Error publishing audit message", e);
			return new ResponseEntity<String>("Failed to process message", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error while handling PageBuiltMessage", e);
			return new ResponseEntity<String>("Failed to process message", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<String> createAndPublishAudit(PageBuiltMessage pageBuiltMessage, PageState pageState, Set<AuditName> auditNames)
		throws JsonProcessingException, ExecutionException, InterruptedException {
		log.info("Received page for auditing, pageId={}, url={}", pageBuiltMessage.getPageId(), pageState.getUrl());

		AuditRecord auditRecord = new PageAuditRecord(
			ExecutionStatus.BUILDING_PAGE,
			new HashSet<>(),
			pageState,
			true,
			auditNames);

		auditRecord = auditRecordService.save(auditRecord);
		auditRecordService.addPageAuditToDomainAudit(pageBuiltMessage.getAuditRecordId(), auditRecord.getId());
		auditRecordService.addPageToAuditRecord(auditRecord.getId(), pageBuiltMessage.getPageId());

		PageAuditMessage auditMessage = new PageAuditMessage(pageBuiltMessage.getAccountId(), auditRecord.getId());
		String auditRecordJson = OUTPUT_MAPPER.writeValueAsString(auditMessage);
		log.info("Sending PageAuditMessage to Pub/Sub for pageAuditId={}", auditRecord.getId());
		auditRecordTopic.publish(auditRecordJson);
		return ResponseEntity.ok("Successfully processed message");
	}

	private Set<AuditName> buildAuditNames(long auditRecordId) {
		Optional<AuditRecord> optRecord = auditRecordService.findById(auditRecordId);
		if (optRecord.isPresent() && optRecord.get() instanceof DomainAuditRecord) {
			DomainAuditRecord record = (DomainAuditRecord) optRecord.get();
			return record.getAuditLabels();
		}
		return buildDefaultAuditNames();
	}

	private Set<AuditName> buildDefaultAuditNames() {
		Set<AuditName> auditNames = new HashSet<>();
		auditNames.add(AuditName.TEXT_BACKGROUND_CONTRAST);
		auditNames.add(AuditName.NON_TEXT_BACKGROUND_CONTRAST);
		auditNames.add(AuditName.LINKS);
		auditNames.add(AuditName.TITLES);
		auditNames.add(AuditName.ENCRYPTED);
		auditNames.add(AuditName.METADATA);
		auditNames.add(AuditName.ALT_TEXT);
		auditNames.add(AuditName.READING_COMPLEXITY);
		auditNames.add(AuditName.PARAGRAPHING);
		auditNames.add(AuditName.IMAGE_COPYRIGHT);
		auditNames.add(AuditName.IMAGE_POLICY);
		return auditNames;
	}

	private boolean hasValidPayload(Body body) {
		return body != null
			&& body.getMessage() != null
			&& body.getMessage().getData() != null
			&& !body.getMessage().getData().isEmpty();
	}

	private String decodePayload(String encodedData) {
		try {
			return new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			log.warn("Received invalid Base64 data from Pub/Sub message", e);
			return null;
		}
	}

	private PageBuiltMessage parseMessage(String payload) {
		try {
			return INPUT_MAPPER.readValue(payload, PageBuiltMessage.class);
		} catch (JsonProcessingException e) {
			log.error("Error occurred while mapping payload to PageBuiltMessage", e);
			return null;
		}
	}

	private ResponseEntity<String> badRequest(String msg) {
		return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
	}
}
