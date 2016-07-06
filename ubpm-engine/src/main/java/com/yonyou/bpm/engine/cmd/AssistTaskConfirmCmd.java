package com.yonyou.bpm.engine.cmd;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.yonyou.bpm.BpmException;
/**
 * 协办确认
 * @author zhaohb
 *
 */
public class AssistTaskConfirmCmd extends NeedsActiveTaskCmd<Void>{


	private static final long serialVersionUID = 1L;
	
	
	public AssistTaskConfirmCmd(String taskId) {
		super(taskId);
	}
	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		//在协办任务上完成
		String createType = (String) task
				.getVariableLocal("createType");
		if(!"assist".equalsIgnoreCase(createType)){
			throw new ActivitiException("任务："+this.taskId+"不是协办任务！");
		} 
		Boolean assistCompleted=(Boolean)task.getVariableLocal("assistCompleted");
		if(assistCompleted==null||!assistCompleted.booleanValue()){
			throw new ActivitiException("协办人未完成！");
		}
		commandContext.getProcessEngineConfiguration().getTaskService().complete(task.getId());
		//协办确认时候，需要将生产的execution删除
		task.getExecution().deleteCascade("assistTaskConfirm");
		return null;
	}

	/**
	 * 在父任务确认
	 * @param commandContext
	 * @param task
	 */
	protected void parentCofirm(CommandContext commandContext, TaskEntity task){

		Boolean assistingConfirm=(Boolean)task.getVariableLocal("assistingConfirm");
		if(assistingConfirm==null||!assistingConfirm.booleanValue()){
			throw new BpmException("不需要协办确认！");
		}
		List<ExecutionEntity>  executions=task.getExecution().getExecutions();
		if(executions==null||executions.size()==0){
			throw new BpmException("没有需要协办确认的任务！");
		}
		for (ExecutionEntity executionEntity : executions) {
			Boolean assist=(Boolean)executionEntity.getVariableLocal("assist");
			if(assist==null||!assist.booleanValue()){
				continue;
			}
			List<TaskEntity> subTasks=executionEntity.getTasks();
			if(subTasks==null||subTasks.size()==0){
				continue;
			}
			for (TaskEntity taskEntity : subTasks) {
				String createType = (String) taskEntity
						.getVariableLocal("createType");
				if(!"assist".equalsIgnoreCase(createType)){
					continue;
				} 
				//正式完成协办产生任务
				commandContext.getProcessEngineConfiguration().getTaskService().complete(taskEntity.getId());
			}
		}
	}
}
