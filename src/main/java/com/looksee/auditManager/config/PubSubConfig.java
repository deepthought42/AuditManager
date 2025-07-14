package com.looksee.auditManager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.looksee.gcp.GoogleCloudStorage;
import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

/**
 * Configuration class for LookseeCore beans that need special configuration.
 * Most beans are now auto-discovered via @ComponentScan.
 */
@Configuration
public class PubSubConfig {

    public PubSubConfig() {
        System.out.println("🔧 PubSubConfig constructor called - configuration is being loaded");
    }

    /**
     * Fallback bean creation if automatic discovery fails.
     * These will only be created if the beans aren't found via component scanning.
     */
    @Bean(name = "audit_record_topic")
    @ConditionalOnMissingBean(name = "audit_record_topic")
    public PubSubPageAuditPublisherImpl auditRecordTopic() {
        try {
            PubSubPageAuditPublisherImpl publisher = new PubSubPageAuditPublisherImpl();
            System.out.println("✅ Created PubSubPageAuditPublisherImpl bean as fallback");
            return publisher;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PubSubPageAuditPublisherImpl: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean(name = "audit_record_service")
    @ConditionalOnMissingBean(name = "audit_record_service")
    public AuditRecordService auditRecordService() {
        try {
            AuditRecordService service = new AuditRecordService();
            System.out.println("✅ Created AuditRecordService bean as fallback");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create AuditRecordService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean(name = "page_state_service")
    @ConditionalOnMissingBean(name = "page_state_service")
    public PageStateService pageStateService() {
        try {
            PageStateService service = new PageStateService();
            System.out.println("✅ Created PageStateService bean as fallback");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PageStateService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // GoogleCloudStorage bean removed - let Spring auto-discovery handle it
    // If needed, it will be configured elsewhere or by Spring Boot auto-configuration

} 