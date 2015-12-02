package com.paradigma.arquitecture;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class StaticApplicationContextAccess.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class StaticApplicationContextAccess implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		StaticApplicationContextAccess.applicationContext = applicationContext;

	}

}
