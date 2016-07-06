package com.yonyou.bpm.engine.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;

import com.yonyou.bpm.core.assign.AssignCheckResult;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.engine.cmd.AssignCheckCmd;
import com.yonyou.bpm.engine.cmd.FindParticipantsCmd;
import com.yonyou.bpm.engine.cmd.JumpToActivityCmd;
import com.yonyou.bpm.engine.cmd.StartProcessInstanceCmd;
import com.yonyou.bpm.engine.impl.BpmProcessInstanceQueryImpl;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmRuntimeService")
public class BpmRuntimeServiceImpl extends RuntimeServiceImpl {

	@TracePoint
	@Override
	public ProcessInstance startProcessInstanceByKey(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY) String processDefinitionKey) {
		return super.startProcessInstanceByKey(processDefinitionKey);
	}

	@TracePoint
	@Override
	public ProcessInstance startProcessInstanceByKey(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY) String processDefinitionKey,
			@TraceValue(TraceKeys.BUSINESS_KEY) String businessKey,
			Map<String, Object> variables) {
		return super.startProcessInstanceByKey(processDefinitionKey,
				businessKey, variables);
	}
	/**
	 * 根据流程定义Key发起带有指派信息的流程
	 * @param processDefinitionKey
	 * @param assignInfo
	 * @return
	 */
	@TracePoint
	public ProcessInstance startProcessInstanceByKey(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY) String processDefinitionKey,AssignInfo assignInfo) {
		return commandExecutor.execute(new StartProcessInstanceCmd<ProcessInstance>(processDefinitionKey, null, null, null, assignInfo));
	}
	@TracePoint
	public ProcessInstance startProcessInstanceById(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_ID) String processDefinitionId,
			@TraceValue(TraceKeys.BUSINESS_KEY) String businessKey,
			Map<String, Object> variables,AssignInfo assignInfo) {
		return commandExecutor.execute(new StartProcessInstanceCmd<ProcessInstance>(null, processDefinitionId, businessKey, variables, assignInfo));
	}
	@TracePoint
	public ProcessInstance startProcessInstanceByKey(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY) String processDefinitionKey,
			@TraceValue(TraceKeys.BUSINESS_KEY) String businessKey,
			Map<String, Object> variables,AssignInfo assignInfo) {
		return commandExecutor.execute(new StartProcessInstanceCmd<ProcessInstance>(processDefinitionKey, null, businessKey, variables, assignInfo));
	}
	@TracePoint
	public ProcessInstance startProcessInstanceByKeyAndTenantId(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY) String processDefinitionKey,
			@TraceValue(TraceKeys.BUSINESS_KEY) String businessKey,
			Map<String, Object> variables, @TraceValue(TraceKeys.TENANT_ID)String tenantId,
			AssignInfo assignInfo) {
		return commandExecutor
				.execute(new StartProcessInstanceCmd<ProcessInstance>(
						processDefinitionKey, null, businessKey, variables,
						tenantId, assignInfo));
	}
	
	/**
	 * 获取活动（activityId）的参与者 
	 * @param processDefinitionId 流程定义ID
	 * @param activityId 活动ID
	 * @param executionId 活动实例ID
	 * @return
	 */
	@TracePoint
	public List<String>  findParticipants(@TraceValue(TraceKeys.PROCESS_DEFINITION_ID)String processDefinitionId, @TraceValue(TraceKeys.ACTIVITY_ID)String activityId ,@TraceValue(TraceKeys.EXECUTION_ID)String executionId){
		return commandExecutor.execute(new FindParticipantsCmd(processDefinitionId, activityId,executionId));
	}
	/**
	 * 检查是否可指派
	 * @param processDefinitionId
	 * @param executionId
	 * @return
	 */
	@TracePoint
	public AssignCheckResult assignCheckById(@TraceValue(TraceKeys.PROCESS_DEFINITION_ID)String processDefinitionId,@TraceValue(TraceKeys.EXECUTION_ID)String executionId){
		return commandExecutor.execute(new AssignCheckCmd(null,processDefinitionId,null, null, executionId));
	}
	/**
	 * 指派检查
	 * @param taskId
	 * @return
	 * 返回空说明不需要指派
	 * AssignCheckResult.isAssignAble:是否需要指派
	 */
	@TracePoint
	public AssignCheckResult assignCheckBykey(@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY)String processDefinitionKey,@TraceValue(TraceKeys.TENANT_ID)String tenantId,@TraceValue(TraceKeys.EXECUTION_ID)String executionId){
		return commandExecutor.execute(new AssignCheckCmd(processDefinitionKey,null, tenantId,null, executionId));
	}
	/**
	 * 跳转到该流程实例内的某个活动
	 * @param processInstanceID
	 * @param activityId
	 */
	@TracePoint
	public void jumpToActivity(@TraceValue(TraceKeys.PROCESS_INSTANCE_ID) String processInstanceId,@TraceValue(TraceKeys.ACTIVITY_ID)String activityId,String jumpOrigin) {
		commandExecutor.execute(new JumpToActivityCmd(processInstanceId,activityId,jumpOrigin));
	}
	/**
	 * 跳转到该流程实例内的某个活动,且指定参与者
	 * @param processInstanceID
	 * @param activityId
	 */
	@TracePoint
	public void jumpToActivity(@TraceValue(TraceKeys.PROCESS_INSTANCE_ID) String processInstanceId,@TraceValue(TraceKeys.ACTIVITY_ID)String activityId,String jumpOrigin,List<Participant> assignParticipants) {
		commandExecutor.execute(new JumpToActivityCmd(processInstanceId,activityId,jumpOrigin,assignParticipants));
	}
	/**
	 * 跳转到流程定义processDefinitionId中的活动并创建新的流程实例
	 * @param processInstanceId
	 * @param processDefinitionId
	 * @param activityId
	 * @param jumpOrigin
	 */
	@TracePoint
	public ExecutionEntity jumpToActivity(@TraceValue(TraceKeys.PROCESS_INSTANCE_ID) String processInstanceId,@TraceValue(TraceKeys.PROCESS_DEFINITION_ID)String processDefinitionId,@TraceValue(TraceKeys.ACTIVITY_ID)String activityId,String jumpOrigin) {
		return commandExecutor.execute(new JumpToActivityCmd(processDefinitionId,  activityId, processInstanceId, jumpOrigin));
	}
	/**
	 * 跳转到流程定义processDefinitionId中的活动并创建新的流程实例,且指定参与者
	 * @param processInstanceId
	 * @param processDefinitionId
	 * @param activityId
	 * @param jumpOrigin
	 */
	@TracePoint
	public ExecutionEntity jumpToActivity(@TraceValue(TraceKeys.PROCESS_INSTANCE_ID) String processInstanceId,@TraceValue(TraceKeys.PROCESS_DEFINITION_ID)String processDefinitionId,@TraceValue(TraceKeys.ACTIVITY_ID)String activityId,String jumpOrigin,List<Participant> assignParticipants) {
		return commandExecutor.execute(new JumpToActivityCmd(processDefinitionId,  activityId, processInstanceId, jumpOrigin,assignParticipants));
	}
	
	public ProcessInstanceQuery createProcessInstanceQuery() {
		return new BpmProcessInstanceQueryImpl(commandExecutor);
	}
}
