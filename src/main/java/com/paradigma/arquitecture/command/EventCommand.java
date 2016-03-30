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
public abstract class EventCommand extends AbstractCommand {

	public EventCommand() {
		super();
	}

	protected AbstractEvent event;

	public AbstractEvent getEvent() {
		return event;
	}

	public void setEvent(AbstractEvent event) {
		this.event = event;
	}

}
