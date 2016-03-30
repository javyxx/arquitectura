package com.paradigma.arquitecture.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import com.paradigma.arquitecture.ArquitectureConfig;
import com.paradigma.arquitecture.configuration.AmqpConfig;

/**
 * The Class EventBus.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class EventBus implements MessageListener {
	private final Log log = LogFactory.getLog(EventBus.class);
	private final ApplicationEventPublisher publisher;
	private final AmqpTemplate amqpTemplate;

	public EventBus(ApplicationEventPublisher publisher, AmqpTemplate amqpTemplate) {
		this.publisher = publisher;
		this.amqpTemplate = amqpTemplate;
	}

	public void publishEvent(ApplicationEvent event) {
		this.publisher.publishEvent(event);
		if (event instanceof RemoteEvent) {
			this.sendMessage(event);
		}
	}

	public void sendMessage(ApplicationEvent event) {
		MessageProperties props = MessagePropertiesBuilder.newInstance()
				.setContentType(MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT)
				.setMessageId(new ObjectId().toString()).setExpiration(AmqpConfig.EXPIRATION_MESSAGE).build();
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		try {
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(event);
		} catch (Throwable e) {
			log.error("Error serialize email ", e);
		}

		Message message = MessageBuilder.withBody(byteOut.toByteArray()).andProperties(props).build();
		amqpTemplate.send(AmqpConfig.EXCHANGE_TOPIC, AmqpConfig.QUEUE_NAME, message);
	}

	@Override
	public void onMessage(Message message) {
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(message.getBody());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			AbstractEvent<?> event = (AbstractEvent<?>) in.readObject();

			if (!event.getApplicationId().equals(ArquitectureConfig.APPLICATION_ID) && (event instanceof SendRemote)) {
				publishEvent(event);
			}
		} catch (Throwable e) {
			log.error("Error parsing message ", e);
		}

	}

}