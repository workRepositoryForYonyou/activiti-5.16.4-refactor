package com.yonyou.bpm.core.category;

import java.util.Date;

/**
 * 目录
 * 
 * @author zhaohb
 *
 */
public interface Category {

	public String getId();

	public void setId(String id);

	public int getRevision();

	public String getCode();

	public void setCode(String code);

	public String getName();

	public void setName(String name);

	public String getType();

	public void setType(String type);

	/**
	 * 排序
	 * 
	 * @return
	 */
	public int getPriority();

	public void setPriority(int priority);

	public Date getCreateTime();

	public void setCreateTime(Date createTime);

	public Date getModifyTime();

	public void setModifyTime(Date modifyTime);

	public String getParent();

	public void setParent(String parent);

	public String getOrg();

	public void setOrg(String org);

	public String getApplication();

	public void setApplication(String application);

	public String getApplication_code();

	public void setApplication_code(String application);

	public boolean isEnable();

	public void setEnable(boolean enable);

	/**
	 * 获取用于认证应用的客户端Token；
	 * 
	 * @return
	 */
	public String getApplicationToken();

	/**
	 * 设置用于认证应用的客户端Token；
	 * 
	 * @param clientToken
	 */
	public void setApplicationToken(String clientToken);
	
	/**
	 * 获取用于认证应用的服务端凭证；
	 * 
	 * @return
	 */
	public String getApplicationCredential();
	
	/**
	 * 设置用于认证应用的服务端凭证；
	 * 
	 * @param credential
	 */
	public void setApplicationCredential(String credential);
	
	/**
	 * 获取允许请求的第三方应用的 IP 白名单;
	 * 
	 * @return
	 */
	public String getIPWhiteList();
	
	/**
	 * 设置允许请求的第三方应用的 IP 白名单;
	 * 
	 * @param ipWhiteList
	 */
	public void setIPWhiteList(String ipWhiteList);

	/**
	 * 租户ID
	 * 
	 * @return
	 */
	public String getTenantId();

	/**
	 * 租户ID
	 * 
	 * @param tenantId
	 */
	public void setTenantId(String tenantId);
}
