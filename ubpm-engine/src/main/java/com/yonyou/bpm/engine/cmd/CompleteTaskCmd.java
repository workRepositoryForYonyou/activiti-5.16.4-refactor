package com.yonyou.bpm.engine.cmd;

import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignInfoItem;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.utils.TaskUtils;

/**
 * 完成任务（带有指派信息）
 * 
 * @author zhaohb
 *
 */
public class CompleteTaskCmd extends
		org.activiti.engine.impl.cmd.CompleteTaskCmd {
	private static final long serialVersionUID = 1L;
	protected AssignInfo assignInfo;
	//评论
	protected String attachmentType;
	protected String taskId;
	protected String messageType;
	protected String message;
	
	public CompleteTaskCmd(String taskId) {
		super(taskId, null);
	}

	public CompleteTaskCmd(String taskId, AssignInfo assignInfo) {
		super(taskId, null);
		this.assignInfo = assignInfo;
	}

	public CompleteTaskCmd(String taskId, Map<String, Object> variables) {
		super(taskId, variables);
	}

	public CompleteTaskCmd(String taskId, Map<String, Object> variables,
			boolean localScope) {
		super(taskId, variables, localScope);
	}

	public CompleteTaskCmd(String taskId, Map<String, Object> variables,
			boolean localScope, AssignInfo assignInfo) {
		super(taskId, variables, localScope);
		this.assignInfo = assignInfo;
	}
	public CompleteTaskCmd(String taskId, Map<String, Object> variables,
			boolean localScope, AssignInfo assignInfo,String messageType, String message) {
		super(taskId, variables, localScope);
		this.assignInfo = assignInfo;
		this.messageType=messageType;
		this.message=message;
	}

	public CompleteTaskCmd(String taskId, Map<String, Object> variables,
			AssignInfo assignInfo) {
		super(taskId, variables);
		this.assignInfo = assignInfo;
	}

	protected Void execute(CommandContext commandContext, TaskEntity task) {
		//保存评论
		if(message!=null&&!"".equals(message.trim())){
			addComment(commandContext,task);
		}
		// 协办人点完成
		boolean isDeal = assistCompleteTask(commandContext, task);
		if (isDeal)
			return null;
		
		subAssistTaskComlplete(commandContext, task);
		
		Object obj = task.getVariableLocal(CounterSignCmd.COUNTERSIGN_ING);
		if (obj != null && ((Boolean) obj).booleanValue()) {
			throw new ActivitiException("正在加签中，请等待加签任务完成！");
		}
		if(assignInfo!=null){
			checkAssignInfo();
		}
		commandContext.addAttribute("currentTask", task);
		if(assignInfo!=null){
			commandContext.addAttribute(AssignInfo.ASSIGNINFO_NEED,true);
			commandContext.addAttribute(AssignInfo.ASSIGNINFO_PARAM, assignInfo);
		}
		return super.execute(commandContext, task);
	}
	//保存评论
	private void addComment(CommandContext commandContext, TaskEntity task){
		commandContext.getProcessEngineConfiguration().getTaskService().addComment(taskId, task.getProcessInstanceId(), messageType, message);
		
	}
	private void checkAssignInfo(){
		boolean hasParticipant=false;
		AssignInfoItem[] assignInfoItems= assignInfo.getAssignInfoItems();
		if(assignInfoItems!=null&&assignInfoItems.length>0){
			for (AssignInfoItem assignInfoItem : assignInfoItems) {
				if(hasParticipant){
					break;
				}
				Participant[] paricipants=assignInfoItem.getParticipants();
				if(paricipants!=null&&paricipants.length>0){
					hasParticipant=true;
					break;
				}
			}
			
		}
		if(!hasParticipant){
			throw new ActivitiException("指派信息中参与者不能为空！");
		}
	}
	//完成子任务为协办的任务
	private void subAssistTaskComlplete(CommandContext commandContext, TaskEntity task) {
		List<ExecutionEntity> executions = task.getExecution().getExecutions();
		if (executions != null && executions.size() > 0) {
			for (ExecutionEntity executionEntity : executions) {
				Boolean assist = (Boolean) executionEntity
						.getVariableLocal("assist");
				if (assist == null || !assist.booleanValue())
					continue;
				List<TaskEntity> tasks = executionEntity.getTasks();
				if (tasks != null && tasks.size() > 0) {
					for (TaskEntity taskEntity : tasks) {
						TaskUtils.complete(taskEntity, variables, localScope,
								false);
					}
					executionEntity.deleteCascade("assistAutoCompleteByParent");
				}
			}
		}
	}

	private boolean assistCompleteTask(CommandContext commandContext,
			TaskEntity task) {
		boolean isDeal = false;
		String createType = (String) task
				.getVariableLocal("createType");
		if ("assist".equalsIgnoreCase(createType)) {
			String parentTaskId = task.getParentTaskId();
			TaskEntity parent = (TaskEntity) commandContext
					.getProcessEngineConfiguration().getTaskService()
					.createTaskQuery().taskId(parentTaskId).singleResult();
			// 协办人点完成，将协办任务给主办人
			if (task.getOwner() == null) {
				String assignee=task.getAssignee();
				if(assignee==null||"".equals(assignee.trim())){
					throw new BpmException("'assignee'不能为空！");
				}
				task.setOwner(assignee);
				String pAssignee=parent.getAssignee();
				if(pAssignee==null||"".equals(pAssignee.trim())){
					throw new BpmException("协办父任务'assignee'不能为空！");
				}
				task.setAssignee(pAssignee);
				task.setVariableLocal("assistCompleted", Boolean.TRUE);
				task.setVariableLocal("completeTime", commandContext.getProcessEngineConfiguration().getClock().getCurrentTime());
				isDeal = true;
			}
			parent.setVariableLocal("assistingConfirm", Boolean.TRUE);
		}
		return isDeal;
	}

	public String getTaskId() {
		return super.getTaskId();
	}

	public AssignInfo getAssignInfo() {
		return this.assignInfo;
	}

}
