package com.paradigma.service.example;

import com.paradigma.arquitecture.event.EventData;
import com.paradigma.arquitecture.event.RemoteEvent;

public class PruebaEvent extends RemoteEvent<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PruebaEvent() {
		super(new EventData());
		// TODO Auto-generated constructor stub
	}

}
