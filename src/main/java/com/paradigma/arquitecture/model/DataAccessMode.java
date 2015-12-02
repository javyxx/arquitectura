package com.paradigma.arquitecture.model;

import com.paradigma.arquitecture.provider.configure.InMemoryProviderConfig;
import com.paradigma.arquitecture.provider.configure.JdbcProviderConfig;
import com.paradigma.arquitecture.provider.configure.MongoProviderConfig;

public enum DataAccessMode {
	CUSTOM(null), MONGO(MongoProviderConfig.class), JDBC(JdbcProviderConfig.class), IN_MEMORY(
			InMemoryProviderConfig.class);

	private final Class<?> configClass;

	public Class<?> getConfigClass() {
		return configClass;
	}

	DataAccessMode(Class<?> configClass) {
		this.configClass = configClass;
	}
}
