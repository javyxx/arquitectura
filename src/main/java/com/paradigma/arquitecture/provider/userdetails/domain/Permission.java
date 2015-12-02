package com.paradigma.arquitecture.provider.userdetails.domain;

import java.util.List;

public class Permission {
	private String resource;
	private String method;
	private List<Object> allowIds;
	private List<Object> denyIds;
	private boolean allowAll;
	private boolean ownEntities;

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Object> getAllowIds() {
		return allowIds;
	}

	public void setAllowIds(List<Object> allowIds) {
		this.allowIds = allowIds;
	}

	public List<Object> getDenyIds() {
		return denyIds;
	}

	public void setDenyIds(List<Object> denyIds) {
		this.denyIds = denyIds;
	}

	public boolean isAllowAll() {
		return allowAll;
	}

	public void setAllowAll(boolean allowAll) {
		this.allowAll = allowAll;
	}

	public boolean isOwnEntities() {
		return ownEntities;
	}

	public void setOwnEntities(boolean ownEntities) {
		this.ownEntities = ownEntities;
	}

}
