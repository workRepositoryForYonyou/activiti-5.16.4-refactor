package com.yonyou.bpm.core.assign;

import java.io.Serializable;
/**
 * 指派信息
 * @author zhaohb
 *
 */
public class AssignInfo implements Serializable{
	public static final String ASSIGNINFO_NEED="bpmAssigninfoNeed_";
	public static final String ASSIGNINFO_PARAM="bpmAssigninfo_";
	public static final String ASSIGNINFO_BY="bpmAssigninfoBy_";
	private static final long serialVersionUID = 1L;
	
	//任务ID
	private String taskId;
	//执行实例ID,开始活动下一步需要指派时需要设置此参数
	private String executionId;
	//指派活动以及参与者信息
	private AssignInfoItem[] assignInfoItems;
	
	/**
	 * 任务ID
	 * @return
	 */
	public String getTaskId() {
		return taskId;
	}
	/**
	 * 任务ID
	 * @param taskId
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	/**
	 * 执行实例ID,开始活动下一步需要指派时需要设置此参数
	 * @return
	 */
	public String getExecutionId() {
		return executionId;
	}
	/**
	 * 执行实例ID,开始活动下一步需要指派时需要设置此参数
	 * @param processInstanceId
	 */
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	/**
	 * 指派活动以及参与者信息
	 * @return
	 */
	public AssignInfoItem[] getAssignInfoItems() {
		return assignInfoItems;
	}
	/**
	 * 指派活动以及参与者信息
	 * @param assignInfoItems
	 */
	public void setAssignInfoItems(AssignInfoItem[] assignInfoItems) {
		this.assignInfoItems = assignInfoItems;
	}
	
}
