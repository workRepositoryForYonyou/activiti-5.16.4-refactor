/**
 * 
 */
package com.yonyou.bpm.core.user.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chouhl
 *
 * 2015年5月21日 下午2:45:11
 */
public class UserEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String code;
	
	private String name;
	
	private String msg;
	
	private int revision;
	
	private String password;
	
	private String mail;
	
	private int rev;
	
	private String salt;
	
	private String org;
	
	private Date createTime;
	private Date modifyTime;
	private boolean enable=true;
	
	public int getRev() {
		return rev;
	}

	public void setRev(int rev) {
		this.rev = rev;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	private String phone;
	
	private String tenantid;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTenantid() {
		return tenantid;
	}

	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}


	
	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
