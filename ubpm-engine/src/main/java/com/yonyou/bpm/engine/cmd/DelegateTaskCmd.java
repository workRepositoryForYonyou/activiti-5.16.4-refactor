package com.yonyou.bpm.engine.cmd;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
/**
 * 待办:withOutDelegationState=true
 * 委托:withOutDelegationState=false(默认为false)
 * @author zhaohb
 *
 */
public class DelegateTaskCmd extends org.activiti.engine.impl.cmd.DelegateTaskCmd{
	private static final long serialVersionUID = 1L;
	protected boolean withOutDelegationState=false;
	
	public DelegateTaskCmd(String taskId, String userId,boolean withOutDelegationState) {
		super(taskId, userId);
		this.withOutDelegationState=withOutDelegationState;
	}
	
	public DelegateTaskCmd(String taskId, String userId) {
		super(taskId, userId);
	}
	
	@Override
	protected Object execute(CommandContext commandContext, TaskEntity task) {
		if (!withOutDelegationState) {
			super.execute(commandContext, task);
		} else {
			if (task.getOwner() == null) {
				task.setOwner(task.getAssignee());
			}
			task.setAssignee(userId, true, true);
		}
		return null;
	}

}
