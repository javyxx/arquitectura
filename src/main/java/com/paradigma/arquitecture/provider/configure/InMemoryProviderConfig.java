package com.paradigma.arquitecture.provider.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.paradigma.arquitecture.configuration.CacheConfig;
import com.paradigma.arquitecture.configuration.security.AccessTokenSecurityConfig;
import com.paradigma.arquitecture.provider.userdetails.service.InMemoryUserDetailsService;

@ConditionalOnMissingBean(AbstractProviderConfig.class)
@Configuration
public class InMemoryProviderConfig implements AbstractProviderConfig {

	@Autowired
	private CacheManager cacheManager;

	@ConditionalOnBean(value = AccessTokenSecurityConfig.class)
	@Bean
	@Primary
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsService(cacheManager);
	}

}
