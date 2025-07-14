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

    @Bean
    @ConditionalOnMissingBean
    public PubSubPageAuditPublisherImpl audit_record_topic() {
        return new PubSubPageAuditPublisherImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditRecordService audit_record_service() {
        return new AuditRecordService();
    }

    @Bean
    @ConditionalOnMissingBean
    public PageStateService page_state_service() {
        return new PageStateService();
    }
} 