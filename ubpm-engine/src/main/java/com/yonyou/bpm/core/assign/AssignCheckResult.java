package com.yonyou.bpm.core.assign;

import java.io.Serializable;
/**
 * 指派检查结果
 * @author zhaohb
 *
 */
public class AssignCheckResult implements Serializable{

	private static final long serialVersionUID = 1L;
	//活动是否可指派
	private boolean isAssignAble=false;
	//指派信息
	private AssignInfo assignInfo;
	//描述
	private String description;
	/**
	 * 活动是否可指派
	 * @return
	 */
	public boolean isAssignAble() {
		return isAssignAble;
	}
	/**
	 * 活动是否可指派
	 * @param isAssignAble
	 */
	public void setAssignAble(boolean isAssignAble) {
		this.isAssignAble = isAssignAble;
	}
	/**
	 * 指派信息
	 * @return
	 */
	public AssignInfo getAssignInfo() {
		return assignInfo;
	}
	/**
	 * 指派信息
	 * @param assignInfo
	 */
	public void setAssignInfo(AssignInfo assignInfo) {
		this.assignInfo = assignInfo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
