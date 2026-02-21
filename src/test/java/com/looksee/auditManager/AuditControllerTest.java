package com.looksee.auditManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.mapper.Body;
import com.looksee.models.PageState;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

	@Mock
	private AuditRecordService auditRecordService;

	@Mock
	private PubSubPageAuditPublisherImpl auditRecordTopic;

	@Mock
	private PageStateService pageStateService;

	private AuditController controller;

	@BeforeEach
	void setup() {
		controller = new AuditController(auditRecordService, auditRecordTopic, pageStateService);
	}

	@Test
	void shouldReturnBadRequestWhenBodyIsMissing() throws Exception {
		ResponseEntity<String> response = controller.receiveMessage(null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void shouldReturnBadRequestWhenMessageDataIsNotBase64() throws Exception {
		Body body = mockBodyWithData("not-base64");

		ResponseEntity<String> response = controller.receiveMessage(body);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(auditRecordTopic, never()).publish(any());
	}

	@Test
	void shouldReturnBadRequestWhenDecodedPayloadIsNotJson() throws Exception {
		String encoded = Base64.getEncoder().encodeToString("invalid-json".getBytes(StandardCharsets.UTF_8));
		Body body = mockBodyWithData(encoded);

		ResponseEntity<String> response = controller.receiveMessage(body);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(auditRecordTopic, never()).publish(any());
	}

	@Test
	void shouldSkipWhenPageAlreadyAudited() throws Exception {
		String payload = "{\"accountId\":1,\"pageId\":2,\"auditRecordId\":3}";
		String encoded = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
		Body body = mockBodyWithData(encoded);

		when(auditRecordService.wasPageAlreadyAudited(3L, 2L)).thenReturn(true);
		when(pageStateService.isPageLandable(2L)).thenReturn(true);
		when(pageStateService.findById(2L)).thenReturn(Optional.of(new PageState()));

		ResponseEntity<String> response = controller.receiveMessage(body);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(auditRecordTopic, never()).publish(any());
	}

	private Body mockBodyWithData(String data) {
		Body body = org.mockito.Mockito.mock(Body.class);
		Body.Message message = org.mockito.Mockito.mock(Body.Message.class);
		when(body.getMessage()).thenReturn(message);
		when(message.getData()).thenReturn(data);
		return body;
	}
}
