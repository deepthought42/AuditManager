package com.looksee.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import com.google.cloud.vision.v1.ImageAnnotatorClient;

/**
 * Initializes the system and launches it. 
 *
 */

@SpringBootApplication(exclude={Neo4jDataAutoConfiguration.class, SecurityAutoConfiguration.class })
@ComponentScan(basePackages = {"com.looksee*"})
@PropertySources({
	@PropertySource("classpath:application.properties"),
	@PropertySource("classpath:auth0.properties")
})
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@ConfigurationProperties("spring.cloud.gcp.vision")
@EnableNeo4jRepositories("com.looksee.models.repository")
@EntityScan(basePackages = { "com.looksee.models"} )
public class EntryPoint {
	
	public static void main(String[] args){
        SpringApplication.run(EntryPoint.class, args);
   	}
	
	@Bean
    @ConditionalOnMissingBean
    public CloudVisionTemplate cloudVisionTemplate(ImageAnnotatorClient imageAnnotatorClient) {
        return new CloudVisionTemplate(imageAnnotatorClient);
    }
}


