package com.yonyou.bpm.core.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
/**
 * 查询
 * @author zhaohb
 *
 */
public class CategoryQueryParam implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String categoryId;
	protected String parentId;;
	protected Set<String> categoryIds;
	//name or code
	protected String keyWord;
	
	protected String name;
	protected String nameLike;
	protected String nameLikeIgnoreCase;
	
	protected String code;
	protected String codeLike;
	protected String codeLikeIgnoreCase;
	
	protected Date createBefore;
	protected Date createAfter;
	
	protected Date modifyBefore;
	protected Date modifyAfter;
	
	protected String tenantId;
	protected String tenantIdLike;
	
	protected boolean withoutTenantId;
	protected boolean withoutChildCategory;
	
	protected boolean enable; 
	protected boolean unable;
	//包含应用
	protected boolean withApplication;
	//应用
    protected String application;
	
	protected int firstResult=0;
	protected int maxResults=Integer.MAX_VALUE;
	
	protected String orderBy;

	

	public CategoryQueryParam categoryId(String categoryId) {
		this.categoryId = categoryId;
		return this;
	}


	public CategoryQueryParam parentId(String parentId) {
		this.parentId = parentId;
		return this;
	}


	public CategoryQueryParam categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}


	public CategoryQueryParam keyWord(String keyWord) {
		this.keyWord = keyWord;
		return this;
	}


	public CategoryQueryParam name(String name) {
		this.name = name;
		return this;
	}


	public CategoryQueryParam nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}


	public CategoryQueryParam nameLikeIgnoreCase(String nameLikeIgnoreCase) {
		this.nameLikeIgnoreCase = nameLikeIgnoreCase;
		return this;
	}


	public CategoryQueryParam code(String code) {
		this.code = code;
		return this;
	}


	public CategoryQueryParam codeLike(String codeLike) {
		this.codeLike = codeLike;
		return this;
	}


	public CategoryQueryParam codeLikeIgnoreCase(String codeLikeIgnoreCase) {
		this.codeLikeIgnoreCase = codeLikeIgnoreCase;
		return this;
	}


	public CategoryQueryParam createBefore(Date createBefore) {
		this.createBefore = createBefore;
		return this;
	}


	public CategoryQueryParam createAfter(Date createAfter) {
		this.createAfter = createAfter;
		return this;
	}


	public CategoryQueryParam modifyBefore(Date modifyBefore) {
		this.modifyBefore = modifyBefore;
		return this;
	}


	public CategoryQueryParam modifyAfter(Date modifyAfter) {
		this.modifyAfter = modifyAfter;
		return this;
	}


	public CategoryQueryParam tenantId(String tenantId) {
		this.tenantId = tenantId;
		return this;
	}


	public CategoryQueryParam tenantIdLike(String tenantIdLike) {
		this.tenantIdLike = tenantIdLike;
		return this;
	}

	public CategoryQueryParam withoutTenantId() {
		this.withoutTenantId=true;
		return this;
	}

	public CategoryQueryParam withoutChildCategory() {
		this.withoutChildCategory = true;
		return this;
	}

	public CategoryQueryParam enable() {
		this.enable = true;
		return this;
	}
	public CategoryQueryParam unable() {
		this.unable = false;
		return this;
	}

	public CategoryQueryParam withApplication() {
		this.withApplication=true;
		return this;
	}

	public CategoryQueryParam application(String application) {
		this.application = application;
		return this;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public CategoryQueryParam firstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public CategoryQueryParam maxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	
	public CategoryQueryParam orderByCreateTimeDesc(){
		this.orderBy="  CREATE_TIME_ DESC";
		return this;
	}
	
	public CategoryQueryParam orderByCreateTimeAsc(){
		this.orderBy="  CREATE_TIME_ ";
		return this;
	}


	public String getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}


	public String getParentId() {
		return parentId;
	}


	public void setParentId(String parentId) {
		this.parentId = parentId;
	}


	public Set<String> getCategoryIds() {
		return categoryIds;
	}


	public void setCategoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
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


	public Date getCreateBefore() {
		return createBefore;
	}


	public void setCreateBefore(Date createBefore) {
		this.createBefore = createBefore;
	}


	public Date getCreateAfter() {
		return createAfter;
	}


	public void setCreateAfter(Date createAfter) {
		this.createAfter = createAfter;
	}


	public Date getModifyBefore() {
		return modifyBefore;
	}


	public void setModifyBefore(Date modifyBefore) {
		this.modifyBefore = modifyBefore;
	}


	public Date getModifyAfter() {
		return modifyAfter;
	}


	public void setModifyAfter(Date modifyAfter) {
		this.modifyAfter = modifyAfter;
	}


	public String getTenantId() {
		return tenantId;
	}


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


	public String getTenantIdLike() {
		return tenantIdLike;
	}


	public void setTenantIdLike(String tenantIdLike) {
		this.tenantIdLike = tenantIdLike;
	}


	public boolean isWithoutTenantId() {
		return withoutTenantId;
	}


	public void setWithoutTenantId(boolean withoutTenantId) {
		this.withoutTenantId = withoutTenantId;
	}


	public boolean isWithoutChildCategory() {
		return withoutChildCategory;
	}


	public void setWithoutChildCategory(boolean withoutChildCategory) {
		this.withoutChildCategory = withoutChildCategory;
	}


	public boolean isEnable() {
		return enable;
	}


	public void setEnable(boolean enable) {
		this.enable = enable;
	}


	public boolean isUnable() {
		return unable;
	}


	public void setUnable(boolean unable) {
		this.unable = unable;
	}


	public boolean isWithApplication() {
		return withApplication;
	}


	public void setWithApplication(boolean withApplication) {
		this.withApplication = withApplication;
	}


	public String getApplication() {
		return application;
	}


	public void setApplication(String application) {
		this.application = application;
	}


	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}


	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	

}
