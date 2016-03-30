package com.paradigma.arquitecture.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import com.paradigma.arquitecture.event.AbstractEvent;

/**
 * The Class Commander.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class Commander implements ApplicationListener<AbstractEvent> {

	private final Log log = LogFactory.getLog(Commander.class);

	private final TaskExecutor taskExecutor;
	private final TaskScheduler taskScheduler;

	public Commander(TaskExecutor taskExecutor, TaskScheduler taskScheduler) {
		super();
		this.taskExecutor = taskExecutor;
		this.taskScheduler = taskScheduler;
	}

	private final Map<String, List<EventCommand>> commands = new HashMap<>();

	public void executeSyncCommand(AbstractCommand command) {
		log.info("Execute command: " + command);
		command.execute();
	}

	public void executeAsyncCommand(AbstractCommand command) {
		log.info("Execute command: " + command);
		taskExecutor.execute(command);
	}

	public void executeCommandAt(AbstractCommand command, Date date) {
		this.taskScheduler.schedule(command, date);
	}

	public void executeCommandOnEvent(String event, EventCommand command) {
		if (commands.containsKey(event)) {
			commands.get(event).add(command);
		} else {
			List<EventCommand> commandList = new ArrayList<>();
			commandList.add(command);
			commands.put(event, commandList);
		}
	}

	@Override
	public void onApplicationEvent(AbstractEvent event) {
		List<EventCommand> commandsList = commands.get(event.getSource());
		if (commandsList != null) {
			for (EventCommand command : commandsList) {
				command.setEvent(event);
				if (event.getMetaData().containsKey("executeAt")) {
					Date executeAt = (Date) event.getMetaData().get("executeAt");
					this.executeCommandAt(command, executeAt);
				} else {
					this.executeAsyncCommand(command);
				}
			}
		}

	}

}
