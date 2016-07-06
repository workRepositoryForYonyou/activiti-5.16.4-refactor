package com.yonyou.bpm.engine.cmd;


import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;

public class GetProcessDefinitionCmd  implements Command<ProcessDefinitionEntity>{
	private String processDefinitionKey;
	private String processDefinitionId;
	private String tenantId;
	private boolean excludeSuspend=true;
	public GetProcessDefinitionCmd(String processDefinitionKey,String processDefinitionId,String tenantId){
		this.processDefinitionKey=processDefinitionKey;
		this.processDefinitionId=processDefinitionId;
		this.tenantId=tenantId;
	}
	public GetProcessDefinitionCmd(String processDefinitionKey,String processDefinitionId,String tenantId,boolean excludeSuspend){
		this.processDefinitionKey=processDefinitionKey;
		this.processDefinitionId=processDefinitionId;
		this.tenantId=tenantId;
		this.excludeSuspend=excludeSuspend;
	}

	@Override
	public ProcessDefinitionEntity execute(CommandContext commandContext) {
		DeploymentManager deploymentCache = commandContext
				.getProcessEngineConfiguration().getDeploymentManager();

		ProcessDefinitionEntity processDefinition = null;
		if (processDefinitionId != null) {
			processDefinition = deploymentCache
					.findDeployedProcessDefinitionById(processDefinitionId);
			if (processDefinition == null) {
				throw new ActivitiObjectNotFoundException(
						"No process definition found for id = '"
								+ processDefinitionId + "'",
						ProcessDefinition.class);
			}
		} else if (processDefinitionKey != null
				&& (tenantId == null || ProcessEngineConfiguration.NO_TENANT_ID
						.equals(tenantId))) {
			processDefinition = deploymentCache
					.findDeployedLatestProcessDefinitionByKey(processDefinitionKey);
			if (processDefinition == null) {
				throw new ActivitiObjectNotFoundException(
						"No process definition found for key '"
								+ processDefinitionKey + "'",
						ProcessDefinition.class);
			}
		} else if (processDefinitionKey != null && tenantId != null
				&& !ProcessEngineConfiguration.NO_TENANT_ID.equals(tenantId)) {
			processDefinition = deploymentCache
					.findDeployedLatestProcessDefinitionByKeyAndTenantId(
							processDefinitionKey, tenantId);
			if (processDefinition == null) {
				throw new ActivitiObjectNotFoundException(
						"No process definition found for key '"
								+ processDefinitionKey
								+ "' for tenant identifier " + tenantId,
						ProcessDefinition.class);
			}
		} else {
			throw new ActivitiIllegalArgumentException(
					"processDefinitionKey and processDefinitionId are null");
		}

		// Do not start process a process instance if the process definition is
		// suspended
		if (processDefinition.isSuspended()&&excludeSuspend) {
			return null;
		}
		return processDefinition;

	}

}
