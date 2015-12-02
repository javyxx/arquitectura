package com.paradigma.arquitecture.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paradigma.arquitecture.event.EventBus;

/**
 * The Class AmqpConfig.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConditionalOnMissingBean(AmqpConfig.class)
@Configuration
public class AmqpConfig {
	public static final String EXCHANGE_TOPIC = "spring-boot-exchange";
	public static final String EXPIRATION_MESSAGE = "5000";

	public static final String QUEUE_NAME = "arquitectureQueue";

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Bean
	public EventBus eventBus() {
		return new EventBus(applicationEventPublisher, amqpTemplate);
	}

	@Bean
	public Queue queue() {
		return new Queue(QUEUE_NAME, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_TOPIC);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_NAME);
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(eventBus());
		return container;
	}
}
