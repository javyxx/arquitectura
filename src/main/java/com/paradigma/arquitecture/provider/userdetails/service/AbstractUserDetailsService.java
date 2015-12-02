package com.paradigma.arquitecture.provider.userdetails.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paradigma.arquitecture.configuration.CacheConfig;
import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetailsImpl;
import com.paradigma.arquitecture.util.Constants;

public abstract class AbstractUserDetailsService
		implements org.springframework.security.core.userdetails.UserDetailsService {
	public static final String BEAN_NAME = "jdbcUserDeailsService";
	private static final int MAX_TIME_IN_CACHE = 10000;
	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

	protected final Cache cache;

	public AbstractUserDetailsService(CacheManager cacheManager) {
		super();
		cache = cacheManager.getCache(Constants.ARQUITECTURE_CACHE_NAME);
	}

	@Override
	public final UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserSecurityDetailsImpl user = cache.get(username, UserSecurityDetailsImpl.class);
		if (user == null || (user != null && user.getLastLoadTime() > MAX_TIME_IN_CACHE)) {
			user = findUser(username);
			cache.put(username, user);
		}

		if (user == null) {
			throw new UsernameNotFoundException("Usuario no encontrado : " + username);
		}

		detailsChecker.check(user);
		return user;
	}

	protected abstract UserSecurityDetailsImpl findUser(String username);

}
