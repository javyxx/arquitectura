package com.paradigma.arquitecture.event;

import java.util.HashMap;
import java.util.Map;
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
public abstract class AbstractEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8329882167757672345L;

	protected UUID applicationId = ArquitectureConfig.APPLICATION_ID;
	protected String eventId = this.getClass().getName();
	public Map<String, Object> metaData = new HashMap<>();
	public Map<String, Object> data = new HashMap<>();


	public AbstractEvent(String eventId) {
		super(eventId);
	}

	public UUID getApplicationId() {
		return applicationId;
	}

	public Object getSource() {
		return source;
	}
	
	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	
}
