package com.paradigma.arquitecture;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.paradigma.arquitecture.configuration.AmqpConfig;
import com.paradigma.arquitecture.configuration.CommanderConfig;
import com.paradigma.arquitecture.configuration.WebSocketConfig;
import com.paradigma.arquitecture.model.DataAccessMode;
import com.paradigma.arquitecture.model.SecurityMode;

/**
 * The Class ArquitectureImportSelector.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class ArquitectureImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(ParadigmaApplication.class.getName()));
		Class<?> customUserDetailsServiceConfig = attributes.getClass("customUserDetailsServiceConfig");
		Class<?> customSegurityConfig = attributes.getClass("customSecurityConfig");

		SecurityMode securityMode = attributes.getEnum("securityMode");
		DataAccessMode dataAccessMode = attributes.getEnum("dataAccessMode");

		boolean enableWebSocket = attributes.getBoolean("enableWebSocket");
		boolean enableCommander = attributes.getBoolean("enableCommander");
		boolean enableAmqpConfig = attributes.getBoolean("enableAmqpConfig");

		List<String> configRoutes = new ArrayList<>();
		if (SecurityMode.CUSTOM.equals(securityMode)) {
			configRoutes.add(customSegurityConfig.getName());
		} else if (SecurityMode.NONE.equals(securityMode)) {
		} else {
			for (Class<?> securityConfigClass : securityMode.getConfigClass()) {
				configRoutes.add(securityConfigClass.getName());
			}
		}

		if (DataAccessMode.CUSTOM.equals(dataAccessMode)) {
			configRoutes.add(customUserDetailsServiceConfig.getName());
		} else {
			configRoutes.add(dataAccessMode.getConfigClass().getName());
		}

		if (enableWebSocket) {
			configRoutes.add(WebSocketConfig.class.getName());
		}
		if (enableCommander) {
			configRoutes.add(CommanderConfig.class.getName());
		}
		if (enableAmqpConfig) {
			configRoutes.add(AmqpConfig.class.getName());
		}

		return configRoutes.toArray(new String[0]);
	}

}
