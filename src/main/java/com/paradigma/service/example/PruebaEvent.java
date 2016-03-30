package com.paradigma.service.example;

import com.paradigma.arquitecture.event.RemoteEvent;

public class PruebaEvent extends RemoteEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PruebaEvent() {
		super(PruebaEvent.class.getName());
	}

}
