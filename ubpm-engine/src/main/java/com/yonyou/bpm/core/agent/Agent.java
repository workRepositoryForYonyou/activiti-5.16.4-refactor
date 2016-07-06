package com.yonyou.bpm.core.agent;

import java.io.Serializable;
import java.util.Date;
/**
 * 代理人设置
 * @author zhaohb
 *
 */
public interface Agent extends Serializable{
	public String getId();
	public void setId(String id);
	/**
	 * 类型
	 * @return
	 */
	public String getType();
	public void setType(String type);
	
	public int getRevision();
	
	/**
	 * 名称，描述
	 * @return
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * 被代理人
	 * @return
	 */
	public String getUserId();
	public void setUserId(String userId);
	/**
	 * 代理人
	 * @return
	 */
	public String getAgentUserId();
	public void setAgentUserId(String agentUserId);
	
	
	public boolean isEnable();
	public void setEnable(boolean enable);
	
	/**
	 * 目录
	 * @return
	 */
	public String getCategoryId();
	public void setCategoryId(String categoryId);
	
	public String getProcessDefinitionKey();
	public void setProcessDefinitionKey(String processDefinitionKey);
	
	public String getProcessDefinitionId();
	public void setProcessDefinitionId(String processDefinitionId);
	
	public String getProcessInstanceId();
	public void setProcessInstanceId(String processInstanceId);
	
	public Date getCreateTime();
	public void setCreateTime(Date createTime);
	public Date getModifyTime();
	public void setModifyTime(Date modifyTime);

}
