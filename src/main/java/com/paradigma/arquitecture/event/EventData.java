package com.paradigma.arquitecture.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class EventData.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 * @param <T>
 *            the generic type
 */
public class EventData<T> implements Serializable {
	public T data;
	public Map<String, Object> metaData = new HashMap<>();

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}
}
