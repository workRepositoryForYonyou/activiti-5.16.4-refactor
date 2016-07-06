package com.yonyou.bpm.core.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
/**
 * 代理人设置查询
 * @author zhaohb
 *
 */
public class AgentQueryParam implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String agentId;
	
	protected String type;
	
	protected String name;
	protected String nameLike;
	protected String nameLikeIgnoreCase;
	
	protected String userId;
	protected String agentUserId;

	protected Date createBefore;
	protected Date createAfter;
	
	protected Date modifyBefore;
	protected Date modifyAfter;
	
	protected boolean enable;
	protected boolean unable;
	
	protected String categoryId;
	protected String processDefinitionKey;
	protected String processDefinitionId;
	protected String processInstanceId;
	
	
	protected Set<String> categoryIds;
	
	protected int firstResult=0;
	protected int maxResults=Integer.MAX_VALUE;
	
	protected String orderBy;

	public String getAgentId() {
		return agentId;
	}
	public AgentQueryParam agentId(String agentId) {
		this.agentId = agentId;
		return this;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getType() {
		return type;
	}
	public AgentQueryParam type(String type) {
		this.type = type;
		return this;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public AgentQueryParam name(String name) {
		this.name = name;
		return this;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getNameLike() {
		return nameLike;
	}
	public AgentQueryParam nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}
	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

	public String getNameLikeIgnoreCase() {
		return nameLikeIgnoreCase;
	}
	public AgentQueryParam nameLikeIgnoreCase(String nameLikeIgnoreCase) {
		this.nameLikeIgnoreCase = nameLikeIgnoreCase;
		return this;
	}
	public void setNameLikeIgnoreCase(String nameLikeIgnoreCase) {
		this.nameLikeIgnoreCase = nameLikeIgnoreCase;
	}

	public String getUserId() {
		return userId;
	}
	public AgentQueryParam userId(String userId) {
		this.userId = userId;
		return this;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAgentUserId() {
		return agentUserId;
	}
	public AgentQueryParam agentUserId(String agentUserId) {
		this.agentUserId = agentUserId;
		return this;
	}
	public void setAgentUserId(String agentUserId) {
		this.agentUserId = agentUserId;
	}

	public Date getCreateBefore() {
		return createBefore;
	}
	public AgentQueryParam createBefore(Date createBefore) {
		this.createBefore = createBefore;
		return this;
	}
	public void setCreateBefore(Date createBefore) {
		this.createBefore = createBefore;
	}

	public Date getCreateAfter() {
		return createAfter;
	}
	public AgentQueryParam createAfter(Date createAfter) {
		this.createAfter = createAfter;
		return this;
	}

	public void setCreateAfter(Date createAfter) {
		this.createAfter = createAfter;
	}

	public Date getModifyBefore() {
		return modifyBefore;
	}
	public AgentQueryParam modifyBefore(Date modifyBefore) {
		this.modifyBefore = modifyBefore;
		return this;
	}
	public void setModifyBefore(Date modifyBefore) {
		this.modifyBefore = modifyBefore;
	}

	public Date getModifyAfter() {
		return modifyAfter;
	}
	public AgentQueryParam modifyAfter(Date modifyAfter) {
		this.modifyAfter = modifyAfter;
		return this;
	}
	public void setModifyAfter(Date modifyAfter) {
		this.modifyAfter = modifyAfter;
	}

	public boolean isEnable() {
		return enable;
	}
	public AgentQueryParam enable() {
		this.enable = true;
		return this;
	}
	public AgentQueryParam unable() {
		this.unable = false;
		return this;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getCategoryId() {
		return categoryId;
	}
	public AgentQueryParam categoryId(String categoryId) {
		this.categoryId = categoryId;
		return this;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public AgentQueryParam processDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
		return this;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public AgentQueryParam processDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
		return this;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public AgentQueryParam processInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
		return this;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public int getFirstResult() {
		return firstResult;
	}
	public AgentQueryParam firstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}
	
	public AgentQueryParam maxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public AgentQueryParam categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}
	public AgentQueryParam orderByCreateTimeAsc(){
		this.orderBy="  CREATE_TIME_ ";
		return this;
	}
	
	
	
}
