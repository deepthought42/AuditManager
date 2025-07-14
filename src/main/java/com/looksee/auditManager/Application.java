package com.looksee.auditManager;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {
    "com.looksee.auditManager",  // Our application package
    "com.looksee.gcp",           // LookseeCore GCP services
    "com.looksee.services",      // LookseeCore services
    "com.looksee.models",        // LookseeCore models (if they have @Component annotations)
    "com.looksee.mapper"         // LookseeCore mappers
})
@PropertySources({
	@PropertySource("classpath:application.properties")
})
@EnableNeo4jRepositories("com.looksee.auditManager.models.repository")
@EntityScan(basePackages = { "com.looksee.auditManager.models"} )
public class Application {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final Random rand = new Random(2020);

	public static void main(String[] args)  {
		SpringApplication.run(Application.class, args);
	}

}
