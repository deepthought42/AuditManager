package com.looksee.auditManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.looksee.gcp.PubSubPageAuditPublisherImpl;

@Configuration
public class PubSubConfig {

    @Bean
    public PubSubPageAuditPublisherImpl pageAuditPublisher() {
        return new PubSubPageAuditPublisherImpl();
    }
} 