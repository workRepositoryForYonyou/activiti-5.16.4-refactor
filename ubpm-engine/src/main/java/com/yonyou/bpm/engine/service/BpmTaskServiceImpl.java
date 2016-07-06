package com.yonyou.bpm.engine.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

import com.yonyou.bpm.core.assign.AssignCheckResult;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.engine.cmd.AssignCheckCmd;
import com.yonyou.bpm.engine.cmd.AssistTaskCmd;
import com.yonyou.bpm.engine.cmd.AssistTaskConfirmCmd;
import com.yonyou.bpm.engine.cmd.AssistTaskRedoCmd;
import com.yonyou.bpm.engine.cmd.CancelAgentCmd;
import com.yonyou.bpm.engine.cmd.CompleteTaskCmd;
import com.yonyou.bpm.engine.cmd.CounterSignCmd;
import com.yonyou.bpm.engine.cmd.DelegateTaskCmd;
import com.yonyou.bpm.engine.cmd.FindAllNextActivitiesCmd;
import com.yonyou.bpm.engine.cmd.FindNextActivitiesCmd;
import com.yonyou.bpm.engine.cmd.InDoingTaskCmd;
import com.yonyou.bpm.engine.cmd.ResolveTaskCmd;
import com.yonyou.bpm.engine.cmd.RetreatCmd;
import com.yonyou.bpm.engine.cmd.WithdrawTaskCmd;
import com.yonyou.bpm.engine.impl.BpmTaskQueryImpl;
import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;
import com.yonyou.bpm.trace.TraceValueResolves;

@TraceTarget("BpmTaskService")
public class BpmTaskServiceImpl extends TaskServiceImpl {
	
	public BpmTaskServiceImpl(ProcessEngineConfigurationImpl configuration) {
		super(configuration);
	}

	@TracePoint
	@Override
	public void claim(@TraceValue(TraceKeys.TASK_ID) String taskId,
			@TraceValue(TraceKeys.USER_ID) String userId) {
		super.claim(taskId, userId);
	}

	@TracePoint
	@Override
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, Object> variables, boolean localScope) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, variables, localScope));
	}

	@TracePoint
	@Override
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, Object> variables) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, variables));
	}

	@TracePoint
	@Override
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId) {
		commandExecutor.execute(new CompleteTaskCmd(taskId));
	}
	@TracePoint
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, Object> variables, boolean localScope,AssignInfo assignInfo) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, variables, localScope, assignInfo));
	}
	@TracePoint
	public void completeWithComment(@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, Object> variables, boolean localScope,AssignInfo assignInfo,String messageType, String message) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, variables, localScope, assignInfo,messageType,message));
	}
	@TracePoint
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, Object> variables,AssignInfo assignInfo) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, variables, assignInfo));
	}

	@TracePoint
	public void complete(@TraceValue(TraceKeys.TASK_ID) String taskId,AssignInfo assignInfo) {
		commandExecutor.execute(new CompleteTaskCmd(taskId, assignInfo));
	}
	@TracePoint
	@Override
	public void deleteTasks(
			@TraceValue(value = TraceKeys.TASK_ID, resolver = TraceValueResolves.COLLECTION_VALUE) Collection<String> taskIds) {
		super.deleteTasks(taskIds);
	}
	
	@TracePoint
	public void withdrawTask(@TraceValue(TraceKeys.TASK_ID) String historyTaskId) {
		commandExecutor.execute(new WithdrawTaskCmd(historyTaskId));
	}
	/**
	 * 查找活动（activityId）的下一步人工活动
	 * @param processDefinitionId
	 * @param activityId
	 * @return
	 */
	@TracePoint
	public List<PvmActivity> findNextActivities(@TraceValue(TraceKeys.TASK_ID) String taskId){
		return commandExecutor.execute(new FindNextActivitiesCmd(taskId));
	}
	
	@TracePoint
	public List<PvmActivity> findAllNextActivities(@TraceValue(TraceKeys.TASK_ID) String taskId){
		return commandExecutor.execute(new FindAllNextActivitiesCmd(taskId));
	}
	
	/**
	 * 加签增加
	 * @param taskId
	 * @param assignee
	 */
	@TracePoint
	public void counterSignAdd(@TraceValue(TraceKeys.TASK_ID)String taskId,List<String> assignees){
		 commandExecutor.execute(new CounterSignCmd("add",assignees,taskId));
	}
	/**
	 * 增加协办
	 * @param taskId
	 * @param assistants
	 * @return
	 */
	@TracePoint
	public Task assitTaskCreate(@TraceValue(TraceKeys.TASK_ID)String taskId,List<String> assistants){
		return  commandExecutor.execute(new AssistTaskCmd(taskId, assistants));
	}
	/**
	 * 协办重做
	 * @param taskId
	 */
	@TracePoint
	public void assitTaskRedo(@TraceValue(TraceKeys.TASK_ID)String taskId){
		  commandExecutor.execute(new AssistTaskRedoCmd(taskId));
	}
	/**
	 * 协办确认
	 * @param taskId
	 */
	@TracePoint
	public void assitTaskConfirm(@TraceValue(TraceKeys.TASK_ID)String taskId){
		  commandExecutor.execute(new AssistTaskConfirmCmd(taskId));
	}
	/**
	 * 取消代理
	 * @param taskId
	 */
	@TracePoint
	public void agentCancel(@TraceValue(TraceKeys.TASK_ID)String taskId){
		  commandExecutor.execute(new CancelAgentCmd(taskId));
	}
	/**
	 * 委托给userId,执行resolveTask后回到owner
	 */
	@Override
	@TracePoint
	public void delegateTask(@TraceValue(TraceKeys.TASK_ID)String taskId, @TraceValue(TraceKeys.TASK_ID)String userId) {
		  commandExecutor.execute(new DelegateTaskCmd(taskId, userId));
    }
	/**
	 * 任务给userId代办，userId可以完成任务，也可以resolveTaskCompletely：拒绝代办
	 * @param taskId
	 * @param userId
	 */
	@TracePoint
	public void delegateTaskCompletely(@TraceValue(TraceKeys.TASK_ID)String taskId, @TraceValue(TraceKeys.TASK_ID)String userId) {
		  commandExecutor.execute(new DelegateTaskCmd(taskId, userId,true));
	}
	/**
	 * 解除委托
	 */
	@TracePoint
	public void resolveTask(@TraceValue(TraceKeys.TASK_ID)String taskId) {
		commandExecutor.execute(new ResolveTaskCmd(taskId, null));
	}
	/**
	 * 解除委托
	 */
	@TracePoint
	public void resolveTask(@TraceValue(TraceKeys.TASK_ID)String taskId, Map<String, Object> variables) {
		commandExecutor.execute(new ResolveTaskCmd(taskId, variables));
	}
	/**
	 * 拒绝代办
	 * @param taskId
	 */
	@TracePoint
	public void resolveTaskCompletely(@TraceValue(TraceKeys.TASK_ID)String taskId) {
		commandExecutor.execute(new ResolveTaskCmd(taskId, null,true));
	}
	/**
	 * 拒绝代办
	 * @param taskId
	 * @param variables
	 */
	@TracePoint
	public void resolveTaskCompletely(@TraceValue(TraceKeys.TASK_ID)String taskId, Map<String, Object> variables) {
		commandExecutor.execute(new ResolveTaskCmd(taskId, variables,true));
	}

	@Override
	public TaskQuery createTaskQuery() {
		 return new BpmTaskQueryImpl(commandExecutor, processEngineConfiguration.getDatabaseType());
	}
	/**
	 * 指派检查
	 * @param taskId
	 * @return
	 * 返回空说明不需要指派
	 * AssignCheckResult.isAssignAble:是否需要指派
	 */
	@TracePoint
	public AssignCheckResult assignCheck(@TraceValue(TraceKeys.TASK_ID)String taskId){
			return commandExecutor.execute(new AssignCheckCmd(taskId));
	}
	/**
	 * 在办
	 * @param taskId
	 */
	@TracePoint
	public void inDoing(@TraceValue(TraceKeys.TASK_ID)String taskId) {
		commandExecutor.execute(new InDoingTaskCmd(taskId));
	}
	
	@TracePoint
	public void retreat(@TraceValue(TraceKeys.TASK_ID)String taskId) {
		commandExecutor.execute(new RetreatCmd(taskId));
	}
}
