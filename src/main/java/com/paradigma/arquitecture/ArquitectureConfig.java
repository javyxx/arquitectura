package com.paradigma.arquitecture;

import java.util.UUID;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.paradigma.arquitecture.configuration.CacheConfig;
import com.paradigma.arquitecture.model.ArquitectureProperties;
import com.paradigma.arquitecture.model.ParadigmaAnnotationAttributes;

/**
 * The Class ArquitectureConfig.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConditionalOnMissingBean(ArquitectureConfig.class)
@Configuration
@EnableAutoConfiguration
@Import({ CacheConfig.class })
public class ArquitectureConfig implements ImportAware {

	public static final UUID APPLICATION_ID = UUID.randomUUID();

	private ParadigmaAnnotationAttributes paradigmaMetadataAnotation;

	@Bean
	public ParadigmaAnnotationAttributes paradigmaMetadataAnotation() {
		return paradigmaMetadataAnotation;
	}

	@Bean
	public ArquitectureProperties arquitectureProperties() {
		return new ArquitectureProperties();
	}

	@Bean
	public StaticApplicationContextAccess staticApplicationContextAccess() {
		return new StaticApplicationContextAccess();
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(importMetadata.getAnnotationAttributes(ParadigmaApplication.class.getName()));
		paradigmaMetadataAnotation = new ParadigmaAnnotationAttributes(attributes);

	}

}
