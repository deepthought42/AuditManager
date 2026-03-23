package com.looksee.auditManager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

class PubSubConfigTest {

	private PubSubConfig config;

	@BeforeEach
	void setUp() {
		config = new PubSubConfig();
	}

	@Test
	void auditRecordTopic_shouldReturnPublisherInstance() {
		try (MockedConstruction<PubSubPageAuditPublisherImpl> mocked =
				mockConstruction(PubSubPageAuditPublisherImpl.class)) {
			PubSubPageAuditPublisherImpl result = config.auditRecordTopic();
			assertNotNull(result);
			assertEquals(1, mocked.constructed().size());
			assertSame(mocked.constructed().get(0), result);
		}
	}

	@Test
	void auditRecordTopic_shouldThrowRuntimeExceptionOnFailure() {
		try (MockedConstruction<PubSubPageAuditPublisherImpl> mocked =
				mockConstruction(PubSubPageAuditPublisherImpl.class,
					(mock, context) -> {
						throw new RuntimeException("GCP not available");
					})) {
			RuntimeException ex = assertThrows(RuntimeException.class, () -> config.auditRecordTopic());
			assertTrue(ex.getMessage().contains("Could not create PubSubPageAuditPublisherImpl"));
			assertNotNull(ex.getCause());
		}
	}

	@Test
	void auditRecordService_shouldReturnServiceInstance() {
		try (MockedConstruction<AuditRecordService> mocked =
				mockConstruction(AuditRecordService.class)) {
			AuditRecordService result = config.auditRecordService();
			assertNotNull(result);
			assertEquals(1, mocked.constructed().size());
			assertSame(mocked.constructed().get(0), result);
		}
	}

	@Test
	void auditRecordService_shouldThrowRuntimeExceptionOnFailure() {
		try (MockedConstruction<AuditRecordService> mocked =
				mockConstruction(AuditRecordService.class,
					(mock, context) -> {
						throw new RuntimeException("Neo4j not available");
					})) {
			RuntimeException ex = assertThrows(RuntimeException.class, () -> config.auditRecordService());
			assertTrue(ex.getMessage().contains("Could not create AuditRecordService"));
			assertNotNull(ex.getCause());
		}
	}

	@Test
	void pageStateService_shouldReturnServiceInstance() {
		try (MockedConstruction<PageStateService> mocked =
				mockConstruction(PageStateService.class)) {
			PageStateService result = config.pageStateService();
			assertNotNull(result);
			assertEquals(1, mocked.constructed().size());
			assertSame(mocked.constructed().get(0), result);
		}
	}

	@Test
	void pageStateService_shouldThrowRuntimeExceptionOnFailure() {
		try (MockedConstruction<PageStateService> mocked =
				mockConstruction(PageStateService.class,
					(mock, context) -> {
						throw new RuntimeException("Neo4j not available");
					})) {
			RuntimeException ex = assertThrows(RuntimeException.class, () -> config.pageStateService());
			assertTrue(ex.getMessage().contains("Could not create PageStateService"));
			assertNotNull(ex.getCause());
		}
	}
}
