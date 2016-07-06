package com.yonyou.bpm.engine.cmd;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
/**
 * 协办重做
 * @author zhaohb
 *
 */
public class AssistTaskRedoCmd extends NeedsActiveTaskCmd<Void>{


	private static final long serialVersionUID = 1L;
	
	
	public AssistTaskRedoCmd(String taskId) {
		super(taskId);
	}
	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		String createType = (String) task
				.getVariableLocal("createType");
		if(!"assist".equalsIgnoreCase(createType)){
			throw new ActivitiException("任务："+this.taskId+"不是协办任务！");
		} 
		Boolean assistCompleted=(Boolean)task.getVariableLocal("assistCompleted");
		if(assistCompleted==null||!assistCompleted.booleanValue()){
			throw new ActivitiException("协办人未完成！");
		}
		//协办重做
		task.setVariableLocal("assistCompleted", Boolean.FALSE);
		task.setAssignee(task.getOwner());
		task.setOwner(null);
		task.update();
		return null;
	}
}
