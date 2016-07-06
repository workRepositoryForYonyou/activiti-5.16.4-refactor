package com.yonyou.bpm.engine.cmd;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
/**
 * 标识在办
 * @author zhaohb
 *
 */
public class InDoingTaskCmd extends NeedsActiveTaskCmd<Void>{

	private static final long serialVersionUID = 1L;
	
	public InDoingTaskCmd(String taskId) {
		super(taskId);
	}
	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		task.setVariableLocal("inDoing",Boolean.TRUE);
		return null;
	}

}
