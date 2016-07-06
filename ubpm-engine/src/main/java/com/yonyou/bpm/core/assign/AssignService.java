package com.yonyou.bpm.core.assign;


/**
 * 指派服务
 * @author zhaohb
 *
 */
public interface AssignService {
	/**
	 * 保存指派信息	
	 * @param assignInfo
	 */
	void save(AssignInfo assignInfo);
	
	/**
	 * 根据任务Id获取指派信息
	 * @param taskId
	 * @return
	 */
	AssignInfo findAssignInfo(String taskId);
	/**
	 * 查询没有跟任务管理的指派信息（开始环节发起需要指派时不跟任务关联）
	 * @param processInstanceId
	 * @return
	 */
	AssignInfo findAssignInfoExcludeTask(String processInstanceId);
	/**
	 * 删除运行时变量表中的指派信息
	 * @param executionId
	 */
	void deleteRunningAssignInfo(String executionId);
	/**
	 *  删除跟任务相关的指派信息
	 * @param taskId
	 * @param isOnlyRunning
	 */
	void deleteAssignInfo(String taskId);
}
