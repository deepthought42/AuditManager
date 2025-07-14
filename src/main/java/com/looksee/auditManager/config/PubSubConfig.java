package com.looksee.auditManager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

/**
 * Configuration class for LookseeCore beans that aren't automatically discovered.
 * This ensures the required beans are available for dependency injection.
 */
@Configuration
public class PubSubConfig {

    public PubSubConfig() {
        System.out.println("🔧 PubSubConfig constructor called - configuration is being loaded");
    }

    @Bean(name = "audit_record_topic")
    public PubSubPageAuditPublisherImpl auditRecordTopic() {
        try {
            PubSubPageAuditPublisherImpl publisher = new PubSubPageAuditPublisherImpl();
            System.out.println("✅ Created PubSubPageAuditPublisherImpl bean successfully");
            return publisher;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PubSubPageAuditPublisherImpl: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean(name = "audit_record_service")
    public AuditRecordService auditRecordService() {
        try {
            AuditRecordService service = new AuditRecordService();
            System.out.println("✅ Created AuditRecordService bean successfully");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create AuditRecordService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean(name = "page_state_service")
    public PageStateService pageStateService() {
        try {
            PageStateService service = new PageStateService();
            System.out.println("✅ Created PageStateService bean successfully");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PageStateService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 