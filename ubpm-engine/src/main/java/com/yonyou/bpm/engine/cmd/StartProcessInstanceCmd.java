package com.yonyou.bpm.engine.cmd;

import java.util.Map;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.activiti.engine.runtime.ProcessInstance;

import com.yonyou.bpm.core.assign.AssignInfo;
/**
 * 发起流程实例（带有指派信息）
 * @author zhaohb
 *
 * @param <T>
 */
public class StartProcessInstanceCmd<T> extends
		org.activiti.engine.impl.cmd.StartProcessInstanceCmd<T> {
	private static final long serialVersionUID = 1L;

	protected AssignInfo assignInfo;

	public StartProcessInstanceCmd(String processDefinitionKey,
			String processDefinitionId, String businessKey,
			Map<String, Object> variables, AssignInfo assignInfo) {
		super(processDefinitionKey, processDefinitionId, businessKey, variables);
		this.assignInfo = assignInfo;
	}

	public StartProcessInstanceCmd(String processDefinitionKey,
			String processDefinitionId, String businessKey,
			Map<String, Object> variables, String tenantId,
			AssignInfo assignInfo) {
		super(processDefinitionKey, processDefinitionId, businessKey,
				variables, tenantId);
		this.assignInfo = assignInfo;
	}

	public StartProcessInstanceCmd(
			ProcessInstanceBuilderImpl processInstanceBuilder,
			AssignInfo assignInfo) {
		super(processInstanceBuilder);
		this.assignInfo = assignInfo;
	}

	public ProcessInstance execute(CommandContext commandContext) {
		commandContext.addAttribute(AssignInfo.ASSIGNINFO_PARAM, assignInfo);
		return super.execute(commandContext);
	}

	public AssignInfo getAssignInfo() {
		return this.assignInfo;
	}
}
