package com.paradigma.arquitecture.model;

import com.paradigma.arquitecture.configuration.security.AccessTokenSecurityConfig;
import com.paradigma.arquitecture.configuration.security.AllowAllSecurityConfig;
import com.paradigma.arquitecture.configuration.security.DenyAllSecurityConfig;

public enum SecurityMode {
	NONE, CUSTOM, ACCESS_TOKEN(AccessTokenSecurityConfig.class), DENY_ALL(DenyAllSecurityConfig.class), ALLOW_ALL(
			AllowAllSecurityConfig.class);

	private final Class<?>[] configClass;

	public Class<?>[] getConfigClass() {
		return configClass;
	}

	SecurityMode(Class<?>... configClass) {
		this.configClass = configClass;
	}
}
