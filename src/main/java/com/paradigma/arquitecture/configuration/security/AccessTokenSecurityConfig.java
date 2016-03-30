package com.paradigma.arquitecture.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.paradigma.arquitecture.auth.accesstoken.StatelessAuthenticationFilter;
import com.paradigma.arquitecture.auth.accesstoken.StatelessLoginFilter;
import com.paradigma.arquitecture.auth.accesstoken.TokenAuthenticationService;
import com.paradigma.arquitecture.auth.permission.PermissionEvaluator;
import com.paradigma.arquitecture.model.ArquitectureProperties;

@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class AccessTokenSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ArquitectureProperties arquitectureProperties;

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return this.userService;
	}

	@Autowired
	private UserDetailsService userService;

	@Bean(name="permissions")
	public PermissionEvaluator permissions() {
		return new PermissionEvaluator();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public TokenAuthenticationService tokenAuthenticationService() {
		String secrect = arquitectureProperties.getSecurity().getAuthentication().getAccessToken().getSecret();
		boolean storeUserInToken = arquitectureProperties.getSecurity().getAuthentication().getAccessToken()
				.isStoreUserInToken();
		return new TokenAuthenticationService(secrect, userService, storeUserInToken);
	}

	public AccessTokenSecurityConfig() {
		super(true);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().and().anonymous().and().servletApi().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()

		// Allow anonymous resource requests
				.antMatchers("/").permitAll().antMatchers("/favicon.ico").permitAll().antMatchers("/**/*.html")
				.permitAll().antMatchers("/**/*.css").permitAll().antMatchers("/**/*.js").permitAll()
				.antMatchers("/api/v1/login").permitAll()

		// Allow anonymous logins
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/actuator", "/autoconfig", "/beans", "/configprops","/docs", "/dump", "/env", "/flyway",
						"/health", "/info", "/liquibase", "/logfile", "/metrics", "/mappings", "/shutdown", "/trace",
						"/refresh").permitAll()
				

		// All other request need to be authenticated
				.anyRequest().authenticated().and()

		// Custom Token based authentication based on the header previously
		// given to the client
				.addFilterBefore(new StatelessAuthenticationFilter(this.tokenAuthenticationService()),
						UsernamePasswordAuthenticationFilter.class)

		// Login to user and add token jwt
				.addFilterBefore(
						new StatelessLoginFilter("/api/v1/login", this.tokenAuthenticationService(),
								this.userDetailsService(), this.authenticationManager()),
						UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}