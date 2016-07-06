package com.yonyou.bpm.engine.cmd;

import java.util.Map;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
/**
 * 决绝代办：withOutResolved=true
 * 完成委托：withOutResolved=false(默认为false)
 * @author zhaohb
 *
 */
public class ResolveTaskCmd extends org.activiti.engine.impl.cmd.ResolveTaskCmd{
	private static final long serialVersionUID = 1L;
	
	protected boolean withOutResolved=false;
	
	public ResolveTaskCmd(String taskId, Map<String, Object> variables,boolean withOutResolved) {
		super(taskId, variables);
		this.withOutResolved=withOutResolved;
	}

	
	public ResolveTaskCmd(String taskId, Map<String, Object> variables) {
		super(taskId, variables);
	}

	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		if(!withOutResolved){
			super.execute(commandContext, task);
		} else {
			if (variables != null) {
				task.setVariables(variables);
			}
			task.setAssignee(task.getOwner(), true, true);
		}
		return null;
	}

}
