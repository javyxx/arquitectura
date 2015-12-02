package com.paradigma.arquitecture.provider.userdetails.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserSecurityDetails extends UserDetails {
	public List<Role> getRoles();

	public Serializable getId();
}
