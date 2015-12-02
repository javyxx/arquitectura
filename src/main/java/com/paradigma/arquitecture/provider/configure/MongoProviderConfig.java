package com.paradigma.arquitecture.provider.configure;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.paradigma.arquitecture.configuration.security.AccessTokenSecurityConfig;
import com.paradigma.arquitecture.model.ArquitectureProperties;
import com.paradigma.arquitecture.provider.userdetails.service.MongoUserDetailsService;

@ConditionalOnMissingBean(AbstractProviderConfig.class)
@Configuration
public class MongoProviderConfig implements AbstractProviderConfig {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private ArquitectureProperties arquitectureProperties;

	@ConditionalOnBean(value = AccessTokenSecurityConfig.class)
	@Bean
	@Primary
	public UserDetailsService userDetailsService() {
		return new MongoUserDetailsService(cacheManager, arquitectureProperties.getData().getMongodb());
	}

}
