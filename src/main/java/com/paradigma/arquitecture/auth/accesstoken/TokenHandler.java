package com.paradigma.arquitecture.auth.accesstoken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.paradigma.arquitecture.provider.userdetails.domain.Permission;
import com.paradigma.arquitecture.provider.userdetails.domain.Role;
import com.paradigma.arquitecture.provider.userdetails.domain.UserAuthentication;
import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetails;
import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	private final String secret;
	private final UserDetailsService userService;

	public TokenHandler(String secret, UserDetailsService userService) {
		this.secret = secret;
		this.userService = userService;
	}

	public UserDetails parseUserFromToken(String token) {
		Jws<Claims> jwt = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
		String username = jwt.getBody().getSubject();
		if (jwt.getBody().containsKey("roles")) {
			List map = (ArrayList) jwt.getBody().get("roles");
			UserDetails userDetails = getUserDetails(username, map);
			return userDetails;
		} else {
			return userService.loadUserByUsername(username);
		}
	}

	public String createTokenForUser(Authentication user) {
		String token = Jwts.builder().setSubject(user.getName()).signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;
	}

	public String createTokenForUserStoreInJwt(UserAuthentication user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", ((UserSecurityDetails) user.getDetails()).getRoles());
		String token = Jwts.builder().setSubject(user.getName()).setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;

	}

	private UserDetails getUserDetails(String username, List<Map<String, Object>> rolesMap) {
		Serializable dbEntity = null;
		boolean accountNonExpired = true;
		boolean accountNonLocked = true;
		boolean credentialsNonExpired = true;
		boolean enabled = true;

		List<Role> roles = new ArrayList<>();
		String password = null;
		for (Map<String, Object> roleMap : rolesMap) {
			String name = (String) roleMap.get("name");
			List<Permission> permissions = new ArrayList<>();
			List<Map<String, Object>> permissionsMap = (List<Map<String, Object>>) roleMap.get("permissions");
			for (Map<String, Object> permissionMap : permissionsMap) {
				Permission permission = new Permission();
				if (permissionMap.containsKey("allowAll")) {
					permission.setAllowAll((boolean) permissionMap.get("allowAll"));
				}
				if (permissionMap.containsKey("method")) {
					permission.setMethod((String) permissionMap.get("method"));
				}
				if (permissionMap.containsKey("ownEntities")) {
					permission.setOwnEntities((boolean) permissionMap.get("ownEntities"));
				}
				if (permissionMap.containsKey("resource")) {
					permission.setResource((String) permissionMap.get("resource"));
				}
				if (permissionMap.containsKey("allowIds")) {
					permission.setAllowIds((List) permissionMap.get("allowIds"));
				}
				if (permissionMap.containsKey("denyIds")) {
					permission.setDenyIds((List) permissionMap.get("denyIds"));
				}
				permissions.add(permission);
			}
			roles.add(new Role(name, permissions));
		}

		UserDetails userDetails = new UserSecurityDetailsImpl(dbEntity, roles, password, username, accountNonExpired,
				accountNonLocked, credentialsNonExpired, enabled);
		return userDetails;
	}

}