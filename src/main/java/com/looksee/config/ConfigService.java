package com.looksee.config;

import static com.looksee.config.SpringExtension.SpringExtProvider;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import akka.actor.ActorSystem;

/**
 * 
 */
@Configuration
@ComponentScan({"com.looksee.models"})
public class ConfigService {
     
	@Autowired
    private ApplicationContext applicationContext;
	
	/**
	 * Actor system singleton for this application.
	 */
	@Bean
	public ActorSystem actorSystem() {
	  ActorSystem system = ActorSystem.create("LookseeActorSystem1");
	  // initialize the application context in the Akka Spring Extension
	  SpringExtProvider.get(system).initialize(applicationContext);
	  return system;
	}
	
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
	    mailSender.setHost("email-smtp.us-east-1.amazonaws.com");
	    mailSender.setPort(587);
	    mailSender.setUsername("AKIAIFMYVAQFASYCGXPA");
	    mailSender.setPassword("BBdxu5oKv9GRFRKIytwrb9NmeYPGtPlJCrES6vEb2TFX");
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	     
	    return mailSender;
	}
}