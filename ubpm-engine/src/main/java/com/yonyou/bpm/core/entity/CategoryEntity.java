package com.yonyou.bpm.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.yonyou.bpm.core.category.Category;
/**
 * 流程目录实体
 * @author zhaohb
 *
 */
public class CategoryEntity implements Serializable,Category {
	private static final long serialVersionUID = 1L;
	private String id;
	private int revision=0;
	private String code;
	private String name;
	private String type;
	private int priority=-1;
	private Date createTime;
	private Date modifyTime;
	private String parent;
	private boolean enable=true;
	private String tenantId;
	
	//新加
	private String org;
	private String org_code;
	private String org_name;
	//新加
	private String application;
	private String application_code;
	private String application_name;
	
	private String applicationToken;
	
	private String credential;
	
	private String ipWhiteList;
	
	public String getApplication_code() {
		return application_code;
	}
	public void setApplication_code(String application_code) {
		this.application_code = application_code;
	}
	public String getApplication_name() {
		return application_name;
	}
	public void setApplication_name(String application_name) {
		this.application_name = application_name;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public CategoryEntity(){
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public int getRevisionNext(){
		return this.revision+1;
	}
	@Override
	public String getApplicationToken() {
		return this.applicationToken;
	}
	@Override
	public void setApplicationToken(String applicationToken) {
		this.applicationToken=applicationToken;
	}
	@Override
	public String getIPWhiteList() {
		return this.ipWhiteList;
	}
	@Override
	public void setIPWhiteList(String ipWhiteList) {
		this.ipWhiteList=ipWhiteList;
	}
	
	@Override
	public String getApplicationCredential() {
		return credential;
	}
	@Override
	public void setApplicationCredential(String credential) {
		this.credential = credential;
	}

}
