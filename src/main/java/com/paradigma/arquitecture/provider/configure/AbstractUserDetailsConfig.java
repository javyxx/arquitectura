package com.paradigma.arquitecture.provider.configure;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AbstractUserDetailsConfig {
	UserDetailsService userDetailsService();
}
