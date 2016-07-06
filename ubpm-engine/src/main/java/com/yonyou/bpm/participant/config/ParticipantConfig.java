package com.yonyou.bpm.participant.config;

import com.yonyou.bpm.participant.adapter.ParticipantAdapter;

/**
 * 参与者配置
 * @author zhaohb
 *
 */
public interface ParticipantConfig {
	/**
	 * 复选
	 */
	public static final String EDITTYPE_CHECKBOX="checkbox";
	/**
	 * 参照
	 */
	public static final String EDITTYPE_REFRENCE="refrence";
	/**
	 * 下拉
	 */
	public static final String EDITTYPE_SELECT="select";
	
	/**主键*/
	String getId();
	void setId(String id);
	/**
	 * 用户、角色等，默认为用户（此种参与者描述的结果）
	 * @return
	 */
	String getType();
	void setType(String type);
	/**
	 * 编码
	 * @return
	 */
	String getCode();
	void setCode(String code);
	/**
	 * 名称
	 * @return
	 */
	String getName();
	void setName(String name);
	
	/**参照,形如basepath?width=200&height=300*/
	String getRefUrl();
	void setRefUrl(String refUrl);
	
	/**查找实现*/
	String getAdapterClazz();
	void setAdapterClazz(String adatperClazz);
	
	/*编辑类型checkbox、ref...*/
	String getEditType();
	void setEditType(String editType);
	
	int getPriority();
	void setPriority(int priority);
	
	boolean isEnable();
	
	ParticipantAdapter getAdapter();

}
