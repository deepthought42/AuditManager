package com.looksee.auditManager.config;

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

    @Bean(name = "audit_record_topic")
    @ConditionalOnMissingBean(name = "audit_record_topic")
    public PubSubPageAuditPublisherImpl auditRecordTopic() {
        return new PubSubPageAuditPublisherImpl();
    }

    @Bean(name = "audit_record_service")
    @ConditionalOnMissingBean(name = "audit_record_service")
    public AuditRecordService auditRecordService() {
        return new AuditRecordService();
    }

    @Bean(name = "page_state_service")
    @ConditionalOnMissingBean(name = "page_state_service")
    public PageStateService pageStateService() {
        return new PageStateService();
    }
}
