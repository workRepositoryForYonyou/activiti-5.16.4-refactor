package com.yonyou.bpm.core.entity;

import java.io.Serializable;


import com.yonyou.bpm.core.category.CategoryLink;
/**
 * 目录权限关联实体
 * @author zhaohb
 *
 */
public class CategoryLinkEntity implements Serializable,CategoryLink{
	private static final long serialVersionUID = 1L;
	private String id;
	private int revision=0;
	private String categoryId;
	private String linkId;
	private String type;
	private String linkName;
	
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
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
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getLinkId() {
		return linkId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRevisionNext(){
		return this.revision+1;
	}
}
