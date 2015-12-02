package com.paradigma.arquitecture;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.paradigma.arquitecture.configuration.security.AccessTokenSecurityConfig;
import com.paradigma.arquitecture.model.DataAccessMode;
import com.paradigma.arquitecture.model.SecurityMode;
import com.paradigma.arquitecture.provider.configure.AbstractUserDetailsConfig;
import com.paradigma.arquitecture.provider.configure.InMemoryProviderConfig;

/**
 * The Interface ParadigmaApplication.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import({ ArquitectureConfig.class, ArquitectureImportSelector.class })
public @interface ParadigmaApplication {
	Class<? extends AbstractUserDetailsConfig>customUserDetailsServiceConfig() default InMemoryProviderConfig.class;

	Class<? extends WebSecurityConfigurerAdapter>customSecurityConfig() default AccessTokenSecurityConfig.class;

	SecurityMode securityMode() default SecurityMode.ACCESS_TOKEN;

	DataAccessMode dataAccessMode() default DataAccessMode.IN_MEMORY;

	boolean enableWebSocket() default true;

	boolean enableCommander() default false;

	boolean enableAmqpConfig() default false;

	String applicationName() default "";
}
