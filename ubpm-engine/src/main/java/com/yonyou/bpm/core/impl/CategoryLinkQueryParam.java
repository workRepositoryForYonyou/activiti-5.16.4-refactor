package com.yonyou.bpm.core.impl;

import java.io.Serializable;
import java.util.Map;

public class CategoryLinkQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String categoryLinkId;
	
	protected String userId;
	protected String roleId;
	protected String[] roleIds;

	protected String categoryId;
	
	protected int firstResult=0;
	protected int maxResults=Integer.MAX_VALUE;
	
	private Map<String,String> others;
	
	
	public String getCategoryLinkId() {
		return categoryLinkId;
	}
	public CategoryLinkQueryParam categoryLinkId(String categoryLinkId) {
		this.categoryLinkId = categoryLinkId;
		return this;
	}
	public void setCategoryLinkId(String categoryLinkId) {
		this.categoryLinkId = categoryLinkId;
	}

	public String getUserId() {
		return userId;
	}
	public CategoryLinkQueryParam userId(String userId) {
		this.userId = userId;
		return this;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}
	public CategoryLinkQueryParam roleId(String roleId) {
		this.roleId = roleId;
		return this;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String[] getRoleIds() {
		return roleIds;
	}
	public CategoryLinkQueryParam roleIds(String[] roleIds) {
		this.roleIds = roleIds;
		return this;
	}
	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	public String getCategoryId() {
		return categoryId;
	}
	public CategoryLinkQueryParam categoryId(String categoryId) {
		this.categoryId = categoryId;
		return this;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public int getFirstResult() {
		return firstResult;
	}
	public CategoryLinkQueryParam firstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}
	public CategoryLinkQueryParam maxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public Map<String, String> getOthers() {
		return others;
	}
	public CategoryLinkQueryParam others(Map<String, String> others) {
		this.others = others;
		return this;
	}
	public void setOthers(Map<String, String> others) {
		this.others = others;
	}


}
