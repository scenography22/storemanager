package com.example.storemanager.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
	// MessageConverter Bean 추가
	@Bean
	public MessageConverter rabbitMessageConverter() {
		// Java Object -> JSON
		return new Jackson2JsonMessageConverter();
	}
}