package com.yonyou.bpm.utils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.calendar.DueDateBusinessCalendar;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLinkType;
/**
 * 任务工具类
 * @author zhaohb
 *
 */
public class TaskUtils {
	public static TaskEntity createAndInsertWithCandidateUsers(ExecutionEntity execution,
			TaskEntity parentTaskEntity, Collection<String> candidateUsers) {
		TaskEntity newTask =  crateAndInserWithOtherInfo(execution, parentTaskEntity);
		newTask.addCandidateUsers(candidateUsers);
		// All properties set, now firing 'create' events
		if (Context.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			Context.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.TASK_CREATED, newTask));
		}
		newTask.fireEvent(TaskListener.EVENTNAME_CREATE);
		return newTask;
	}
	public static TaskEntity createAndInsertWithOwner(ExecutionEntity execution,
			TaskEntity parentTaskEntity, String owner) {
		TaskEntity newTask = crateAndInserWithOtherInfo(execution, parentTaskEntity);
		newTask.setOwner(owner);
		// All properties set, now firing 'create' events
		if (Context.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			Context.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.TASK_CREATED, newTask));
		}
		newTask.fireEvent(TaskListener.EVENTNAME_CREATE);
		return newTask;
	}
	@SuppressWarnings("rawtypes")
	public static void complete(TaskEntity taskEntity, Map variablesMap, boolean localScope,boolean signal) {
	  	
	  	if (taskEntity.getDelegationState() != null && taskEntity.getDelegationState().equals(DelegationState.PENDING)) {
	  		throw new ActivitiException("A delegated task cannot be completed, but should be resolved instead.");
	  	}
	  	
	  	taskEntity.fireEvent(TaskListener.EVENTNAME_COMPLETE);

	    if (Authentication.getAuthenticatedUserId() != null && taskEntity.getProcessInstanceId() != null) {
	    	taskEntity.getProcessInstance().involveUser(Authentication.getAuthenticatedUserId(), IdentityLinkType.PARTICIPANT);
	    }
	    
	    if(Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
	    	Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	    	    ActivitiEventBuilder.createEntityWithVariablesEvent(ActivitiEventType.TASK_COMPLETED, taskEntity, variablesMap, localScope));
	    }
	 
	    Context
	      .getCommandContext()
	      .getTaskEntityManager()
	      .deleteTask(taskEntity, TaskEntity.DELETE_REASON_COMPLETED, false);
	    
	    if (taskEntity.getExecutionId()!=null) {
	      ExecutionEntity execution = taskEntity.getExecution();
	      execution.removeTask(taskEntity);
	      if(signal){
	    	  execution.signal(null, null);
	      }
	    }
	  }
	public static TaskEntity createAndInsert(ExecutionEntity execution,
			TaskEntity parentTaskEntity, String assigneeTemp) {
		TaskEntity newTask = crateAndInserWithOtherInfo(execution, parentTaskEntity);
		newTask.setAssignee(assigneeTemp);
		// All properties set, now firing 'create' events
		if (Context.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			Context.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.TASK_CREATED, newTask));
		}
		newTask.fireEvent(TaskListener.EVENTNAME_CREATE);
		return newTask;
	}
	private static TaskEntity crateAndInserWithOtherInfo(ExecutionEntity execution,
			TaskEntity parentTaskEntity){
		TaskEntity newTask=TaskEntity.createAndInsert(execution);
		newTask.setParentTaskId(parentTaskEntity.getId());
		newTask.setExecution(execution);
		newTask.setProcessInstanceId(execution.getProcessInstanceId());
		newTask.setProcessDefinitionId(execution.getProcessDefinitionId());
		newTask.setExecutionId(execution.getId());
		newTask.setTaskDefinition(parentTaskEntity.getTaskDefinition());

		TaskDefinition taskDefinition = newTask.getTaskDefinition();
		if (taskDefinition.getNameExpression() != null) {
			String name = (String) taskDefinition.getNameExpression().getValue(
					execution);
			newTask.setName(name);
		}

		if (taskDefinition.getDescriptionExpression() != null) {
			String description = (String) taskDefinition
					.getDescriptionExpression().getValue(execution);
			newTask.setDescription(description);
		}

		if (taskDefinition.getDueDateExpression() != null) {
			Object dueDate = taskDefinition.getDueDateExpression().getValue(
					execution);
			if (dueDate != null) {
				if (dueDate instanceof Date) {
					newTask.setDueDate((Date) dueDate);
				} else if (dueDate instanceof String) {
					BusinessCalendar businessCalendar = Context
							.getProcessEngineConfiguration()
							.getBusinessCalendarManager()
							.getBusinessCalendar(DueDateBusinessCalendar.NAME);
					newTask.setDueDate(businessCalendar
							.resolveDuedate((String) dueDate));
				} else {
					throw new ActivitiIllegalArgumentException(
							"Due date expression does not resolve to a Date or Date string: "
									+ taskDefinition.getDueDateExpression()
											.getExpressionText());
				}
			}
		}

		if (taskDefinition.getPriorityExpression() != null) {
			final Object priority = taskDefinition.getPriorityExpression()
					.getValue(execution);
			if (priority != null) {
				if (priority instanceof String) {
					try {
						newTask.setPriority(Integer.valueOf((String) priority));
					} catch (NumberFormatException e) {
						throw new ActivitiIllegalArgumentException(
								"Priority does not resolve to a number: "
										+ priority, e);
					}
				} else if (priority instanceof Number) {
					newTask.setPriority(((Number) priority).intValue());
				} else {
					throw new ActivitiIllegalArgumentException(
							"Priority expression does not resolve to a number: "
									+ taskDefinition.getPriorityExpression()
											.getExpressionText());
				}
			}
		}

		if (taskDefinition.getCategoryExpression() != null) {
			final Object category = taskDefinition.getCategoryExpression()
					.getValue(execution);
			if (category != null) {
				if (category instanceof String) {
					newTask.setCategory((String) category);
				} else {
					throw new ActivitiIllegalArgumentException(
							"Category expression does not resolve to a string: "
									+ taskDefinition.getCategoryExpression()
											.getExpressionText());
				}
			}
		}

		if (taskDefinition.getFormKeyExpression() != null) {
			final Object formKey = taskDefinition.getFormKeyExpression()
					.getValue(execution);
			if (formKey != null) {
				if (formKey instanceof String) {
					newTask.setFormKey((String) formKey);
				} else {
					throw new ActivitiIllegalArgumentException(
							"FormKey expression does not resolve to a string: "
									+ taskDefinition.getFormKeyExpression()
											.getExpressionText());
				}
			}
		}
		return newTask;
	}

}
