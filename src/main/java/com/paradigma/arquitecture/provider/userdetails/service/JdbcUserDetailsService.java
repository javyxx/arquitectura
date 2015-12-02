package com.paradigma.arquitecture.provider.userdetails.service;

import org.springframework.cache.CacheManager;

import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetailsImpl;

public class JdbcUserDetailsService extends AbstractUserDetailsService {

	public static final String BEAN_NAME = "jdbcUserDeailsService";

	public JdbcUserDetailsService(CacheManager cacheManager) {
		super(cacheManager);
	}

	protected UserSecurityDetailsImpl findUser(String username) {
		// TODO:implementar
		return null;
	}

}