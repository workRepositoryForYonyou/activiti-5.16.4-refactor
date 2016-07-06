package com.yonyou.bpm.engine.cmd;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import com.yonyou.bpm.BpmException;

public class CancelAgentCmd extends NeedsActiveTaskCmd<Void> {
	private static final long serialVersionUID = 1L;
	
	public CancelAgentCmd(String taskId) {
		super(taskId);
	}


	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		String newAssignee=(String)task.getVariableLocal("redirectUser");
		if(newAssignee==null||"".equals(newAssignee.trim())){
			throw new BpmException("没有代理人！");
		}
		//清空带来人变量
		task.setVariableLocal("redirectUser", "");
		task.setAssignee(task.getOwner());
		task.update();
		return null;
	}

}
