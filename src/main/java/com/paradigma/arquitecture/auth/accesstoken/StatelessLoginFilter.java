package com.paradigma.arquitecture.auth.accesstoken;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradigma.arquitecture.provider.userdetails.domain.UserAuthentication;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserDetailsService userDetailsService;

	public StatelessLoginFilter(final String urlMapping, final TokenAuthenticationService tokenAuthenticationService,
			final UserDetailsService userDetailsService2, final AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(urlMapping));
		this.userDetailsService = userDetailsService2;
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		final HashMap<String, Object> user = new ObjectMapper().readValue(request.getInputStream(), typeRef);
		final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
				user.get("username"), user.get("password"));
		return this.getAuthenticationManager().authenticate(loginToken);
	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final Authentication authentication) throws IOException, ServletException {

		// Lookup the complete User object from the database and create an
		// Authentication for it
		final UserDetails authenticatedUser = this.userDetailsService.loadUserByUsername(authentication.getName());
		final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		// Add the custom token as HTTP header to the response
		this.tokenAuthenticationService.addAuthentication(response, userAuthentication);

		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
	}
}