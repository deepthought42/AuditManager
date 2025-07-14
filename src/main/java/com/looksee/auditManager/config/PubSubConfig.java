package com.looksee.auditManager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.looksee.gcp.PubSubPageAuditPublisherImpl;
import com.looksee.services.AuditRecordService;
import com.looksee.services.PageStateService;

/**
 * Configuration class for LookseeCore beans.
 * This provides manual bean definitions for the required LookseeCore components
 * to work around auto-configuration circular import issues.
 */
@Configuration
public class PubSubConfig {

    @Value("${spring.cloud.gcp.project-id:test-project}")
    private String projectId;

    @Value("${pubsub.page_audit_topic:page-audit-topic}")
    private String pageAuditTopic;

    @Value("${pubsub.audit_update:audit-update-topic}")
    private String auditUpdateTopic;

    @Value("${pubsub.error_topic:audit-error-topic}")
    private String errorTopic;

    public PubSubConfig() {
        System.out.println("🔧 PubSubConfig constructor called - configuring LookseeCore beans manually");
    }

    /**
     * Creates the main PubSub publisher bean for audit messages.
     * This bean is used by AuditController to publish audit messages.
     */
    @Bean(name = "audit_record_topic")
    @ConditionalOnMissingBean(name = "audit_record_topic")
    public PubSubPageAuditPublisherImpl auditRecordTopic() {
        try {
            System.out.println("🚀 Creating PubSubPageAuditPublisherImpl with project: " + projectId + ", topic: " + pageAuditTopic);
            
            // Create the publisher - LookseeCore should handle the internal configuration
            PubSubPageAuditPublisherImpl publisher = new PubSubPageAuditPublisherImpl();
            
            System.out.println("✅ Successfully created PubSubPageAuditPublisherImpl bean");
            return publisher;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PubSubPageAuditPublisherImpl: " + e.getMessage());
            e.printStackTrace();
            
            // For development/testing, create a stub implementation if needed
            System.out.println("⚠️  Creating fallback stub implementation for development");
            throw new RuntimeException("Could not create PubSubPageAuditPublisherImpl. Check GCP configuration.", e);
        }
    }

    /**
     * Creates the AuditRecordService bean for managing audit records.
     */
    @Bean(name = "audit_record_service")
    @ConditionalOnMissingBean(name = "audit_record_service")
    public AuditRecordService auditRecordService() {
        try {
            System.out.println("🚀 Creating AuditRecordService");
            
            AuditRecordService service = new AuditRecordService();
            
            System.out.println("✅ Successfully created AuditRecordService bean");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create AuditRecordService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not create AuditRecordService. Check Neo4j configuration.", e);
        }
    }

    /**
     * Creates the PageStateService bean for managing page state information.
     */
    @Bean(name = "page_state_service")
    @ConditionalOnMissingBean(name = "page_state_service")
    public PageStateService pageStateService() {
        try {
            System.out.println("🚀 Creating PageStateService");
            
            PageStateService service = new PageStateService();
            
            System.out.println("✅ Successfully created PageStateService bean");
            return service;
        } catch (Exception e) {
            System.err.println("❌ Failed to create PageStateService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not create PageStateService. Check Neo4j configuration.", e);
        }
    }
}