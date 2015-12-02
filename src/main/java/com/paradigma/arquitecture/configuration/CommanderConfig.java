package com.paradigma.arquitecture.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.paradigma.arquitecture.command.Commander;

/**
 * The Class CommanderConfig.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConditionalOnMissingBean(CommanderConfig.class)
@Configuration
@EnableAsync
@EnableScheduling
public class CommanderConfig {

	@Autowired
	@Qualifier("clientOutboundChannelExecutor")
	private TaskExecutor taskExecutor;

	@Autowired
	private TaskScheduler taskScheduler;

	@Bean
	public Commander commander() {
		return new Commander(taskExecutor, taskScheduler);
	}

	@Bean
	@Primary
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

}
