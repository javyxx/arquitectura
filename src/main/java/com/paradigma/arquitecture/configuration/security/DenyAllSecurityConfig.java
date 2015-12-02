package com.paradigma.arquitecture.configuration.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
@Configuration
@EnableWebSecurity
@Order(1)
public class DenyAllSecurityConfig extends WebSecurityConfigurerAdapter {

	public DenyAllSecurityConfig() {
		super(true);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().and().anonymous().and().servletApi().and().authorizeRequests()

		// Allow anonymous resource requests
				.antMatchers("/**").denyAll();
	}

}