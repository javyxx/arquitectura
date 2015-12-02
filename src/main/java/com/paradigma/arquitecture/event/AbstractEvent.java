package com.paradigma.arquitecture.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.paradigma.arquitecture.ArquitectureConfig;

/**
 * The Class AbstractEvent.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 * @param <T>
 *            the generic type
 */
public abstract class AbstractEvent<T> extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8329882167757672345L;

	protected UUID applicationId = ArquitectureConfig.APPLICATION_ID;
	protected EventData<T> source;

	public AbstractEvent(EventData<T> source) {
		super(source);
		this.source = source;
	}

	public UUID getApplicationId() {
		return applicationId;
	}

	public EventData<T> getSource() {
		return source;
	}

}
