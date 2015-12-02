package com.paradigma.arquitecture.command;

import com.paradigma.arquitecture.event.AbstractEvent;

/**
 * The Class EventCommand.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 * @param <T>
 *            the generic type
 */
public abstract class EventCommand<T> extends AbstractCommand {

	public EventCommand() {
		super();
	}

	protected AbstractEvent<T> event;

	public AbstractEvent<T> getEvent() {
		return event;
	}

	public void setEvent(AbstractEvent<T> event) {
		this.event = event;
	}

}
