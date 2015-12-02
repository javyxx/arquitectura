package com.paradigma.arquitecture.auth.accesstoken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.paradigma.arquitecture.provider.userdetails.domain.UserAuthentication;

public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	private final boolean storeUserInToken;

	public TokenAuthenticationService(String secret, UserDetailsService userService, boolean storeUserInToken) {
		tokenHandler = new TokenHandler(secret, userService);
		this.storeUserInToken = storeUserInToken;
	}

	public String addAuthentication(HttpServletResponse response, UserAuthentication authentication) {
		String token = "";
		if (storeUserInToken) {
			token = tokenHandler.createTokenForUserStoreInJwt(authentication);
		} else {
			token = tokenHandler.createTokenForUser(authentication);
		}
		response.addHeader(AUTH_HEADER_NAME, token);
		return token;
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		if (token != null) {
			final UserDetails user = tokenHandler.parseUserFromToken(token);
			return new UserAuthentication(user);
		}
		return null;
	}
}