package com.yonyou.bpm.core.category;
/**
 * 目录关联
 * @author zhaohb
 *
 */
public interface CategoryLink {
	
	public String getId();
	public void setId(String id);
	
	public int getRevision();
	
	public String getCategoryId();
	public void setCategoryId(String categoryId);
	
	public String getLinkId();
	public void setLinkId(String linkId);
	
	public String getType();
	public void setType(String type);
	
	public String getLinkName();
	public void setLinkName(String linkName);

}
