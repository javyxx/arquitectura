package com.paradigma.arquitecture.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * The Class WebSocketConfig.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConditionalOnMissingBean(WebSocketConfig.class)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(final MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
	}

	@Override
	public void registerStompEndpoints(final StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
	}

}