package com.paradigma.arquitecture.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paradigma.arquitecture.util.Constants;

/**
 * The Class CacheConfig.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConditionalOnMissingBean(CacheConfig.class)
@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager(Constants.ARQUITECTURE_CACHE_NAME);
	}
}
