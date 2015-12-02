package com.paradigma.arquitecture.auth.permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.paradigma.arquitecture.provider.userdetails.domain.Permission;
import com.paradigma.arquitecture.provider.userdetails.domain.Role;
import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetails;

/**
 * The Class PermissionEvaluator.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class PermissionEvaluator {

	public UserSecurityDetails getCurrentUser() {
		final Authentication authentication = getAuthenticate();
		if (authentication != null && authentication.getDetails() instanceof UserSecurityDetails) {
			return (UserSecurityDetails) authentication.getDetails();
		}
		return null;
	}

	private Authentication getAuthenticate() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication;
	}

	public boolean isOwner(Object id) {
		return getCurrentUser().getId().equals(id);
	}

	public boolean allowAny(String resource, String method) {
		List<Permission> permissionsApply = getPermissionsFor(resource, method);
		return !permissionsApply.isEmpty();
	}

	public boolean allow(Serializable id, String resource, String method) {
		List<Permission> permissionsApply = getPermissionsFor(resource, method);
		if (getDennyFor(id, permissionsApply)) {
			return false;
		}
		List<Permission> permissionsAll = getAllowAll(permissionsApply);
		if (!permissionsAll.isEmpty()) {
			return true;
		}

		if (getAllowFor(id, permissionsApply)) {
			return false;
		}

		return false;
	}

	private List<Permission> getAllowAll(List<Permission> permissionsApply) {
		List<Permission> permissionsAll = permissionsApply.stream().filter(permission -> permission.isAllowAll())
				.collect(Collectors.toList());
		return permissionsAll;
	}

	private boolean getAllowFor(Object id, List<Permission> permissions) {
		List<Permission> permissionsApply = permissions.stream()
				.filter(permission -> permission.getAllowIds().contains(id)).collect(Collectors.toList());
		return !permissionsApply.isEmpty();
	}

	private boolean getDennyFor(Object id, List<Permission> permissions) {
		List<Permission> permissionsApply = permissions.stream()
				.filter(permission -> permission.getDenyIds().contains(id)).collect(Collectors.toList());
		return !permissionsApply.isEmpty();
	}

	private List<Permission> getPermissionsFor(String resource, String method) {
		List<Permission> permissions = new ArrayList<>();
		for (Role role : this.getCurrentUser().getRoles()) {
			permissions.addAll(role.getPermissions());
		}

		List<Permission> permissionsApply = permissions.stream().filter(
				permission -> permission.getResource().equals(resource) && permission.getMethod().equals(method))
				.collect(Collectors.toList());
		return permissionsApply;
	}

}
