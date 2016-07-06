package com.yonyou.bpm.core.user.impl;

import java.io.Serializable;
/**
 * 查询
 * @author zhuyjh
 *
 */
public class UserQueryParam implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String id;
	//name or code
	protected String keyWord;
	
	protected String name;
	protected String nameLike;
	protected String nameLikeIgnoreCase;
	
	protected String code;
	protected String codeLike;
	protected String codeLikeIgnoreCase;
	
	
	protected String tenantid;
	protected String tenantIdLike;
	
	protected String mail;
	protected String phone;
	

	
	protected int firstResult=0;
	protected int maxResults=Integer.MAX_VALUE;
	
	protected String orderBy;

	

	public UserQueryParam categoryId(String id) {
		this.id = id;
		return this;
	}

	public UserQueryParam keyWord(String keyWord) {
		this.keyWord = keyWord;
		return this;
	}


	public UserQueryParam name(String name) {
		this.name = name;
		return this;
	}


	public UserQueryParam nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}


	public UserQueryParam nameLikeIgnoreCase(String nameLikeIgnoreCase) {
		this.nameLikeIgnoreCase = nameLikeIgnoreCase;
		return this;
	}


	public UserQueryParam code(String code) {
		this.code = code;
		return this;
	}


	public UserQueryParam codeLike(String codeLike) {
		this.codeLike = codeLike;
		return this;
	}


	public UserQueryParam codeLikeIgnoreCase(String codeLikeIgnoreCase) {
		this.codeLikeIgnoreCase = codeLikeIgnoreCase;
		return this;
	}

	public UserQueryParam tenantId(String tenantid) {
		this.tenantid = tenantid;
		return this;
	}


	public UserQueryParam tenantIdLike(String tenantIdLike) {
		this.tenantIdLike = tenantIdLike;
		return this;
	}
	
	public UserQueryParam mail(String mail) {
		this.mail = mail;
		return this;
	}
	
	public UserQueryParam phone(String phone) {
		this.phone = phone;
		return this;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public UserQueryParam firstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public UserQueryParam maxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	
	public UserQueryParam orderByCreateTimeDesc(){
		this.orderBy="  CREATE_TIME_ DESC";
		return this;
	}
	
	public UserQueryParam orderByCreateTimeAsc(){
		this.orderBy="  CREATE_TIME_ ";
		return this;
	}


	public String getKeyWord() {
		return keyWord;
	}


	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getNameLike() {
		return nameLike;
	}


	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}


	public String getNameLikeIgnoreCase() {
		return nameLikeIgnoreCase;
	}


	public void setNameLikeIgnoreCase(String nameLikeIgnoreCase) {
		this.nameLikeIgnoreCase = nameLikeIgnoreCase;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getCodeLike() {
		return codeLike;
	}


	public void setCodeLike(String codeLike) {
		this.codeLike = codeLike;
	}


	public String getCodeLikeIgnoreCase() {
		return codeLikeIgnoreCase;
	}


	public void setCodeLikeIgnoreCase(String codeLikeIgnoreCase) {
		this.codeLikeIgnoreCase = codeLikeIgnoreCase;
	}

	public String getTenantId() {
		return tenantid;
	}

	public void setTenantId(String tenantid) {
		this.tenantid = tenantid;
	}

	public String getTenantIdLike() {
		return tenantIdLike;
	}

	public void setTenantIdLike(String tenantIdLike) {
		this.tenantIdLike = tenantIdLike;
	}
	
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}


}
