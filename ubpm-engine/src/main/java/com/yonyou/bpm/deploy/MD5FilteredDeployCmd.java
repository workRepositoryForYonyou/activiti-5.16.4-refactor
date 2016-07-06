package com.yonyou.bpm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.cmd.ActivateProcessDefinitionCmd;
import org.activiti.engine.impl.cmd.DeploymentSettings;
import org.activiti.engine.impl.cmd.SuspendProcessDefinitionCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.ResourceEntity;
import org.activiti.engine.impl.repository.DeploymentBuilderImpl;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.utils.EncodingUtil;

public class MD5FilteredDeployCmd implements Command<Deployment>,
		Serializable {

	private static final long serialVersionUID = 1531083355549927500L;

	private Logger logger = LoggerFactory
			.getLogger(MD5FilteredDeployCmd.class);

	protected DeploymentBuilderImpl deploymentBuilder;

	public MD5FilteredDeployCmd(DeploymentBuilderImpl deploymentBuilder) {
		this.deploymentBuilder = deploymentBuilder;
	}

	public Deployment execute(CommandContext commandContext) {
		DeploymentEntity deployment = deploymentBuilder.getDeployment();

		deployment.setDeploymentTime(commandContext
				.getProcessEngineConfiguration().getClock().getCurrentTime());

		Map<String, ResourceEntity> targetDeployResouces = deployment.getResources();;
		if (deploymentBuilder.isDuplicateFilterEnabled()) {
			List<ByteArrayEntity> md5Resources = commandContext
					.getProcessEngineConfiguration().getManagementService()
					.executeCustomSql(new ProcDefMD5QueryExecution());
			Set<String> deployedMD5Set = new HashSet<String>();
			for (ByteArrayEntity md5Res : md5Resources) {
				if (md5Res.getBytes() != null) {
					deployedMD5Set.add(EncodingUtil.base64BytesToString(md5Res.getBytes()));
				}
			}

			Map<String, ResourceEntity> deployResouces = deployment.getResources();
			Map<String, ResourceEntity> filteredResouces = new HashMap<String, ResourceEntity>();
			for (String resName : deployResouces.keySet()) {
				ResourceEntity resource = deployResouces.get(resName);
				if (isBpmnResource(resource)) {
					byte[] md5Base64Bytes = EncodingUtil.generateMD5Base64Bytes(resource.getBytes());
					String md5String = EncodingUtil.base64BytesToString(md5Base64Bytes);
					if (deployedMD5Set.contains(md5String)) {
						// 流程资源已部署，忽略该流程；
						logger.warn("检测发现流程定义“{}”已经存在，忽略该流程文件。", resName);
						continue;
					}else{
						// 添加流程定义的 MD5资源；
						ResourceEntity md5Entry = new ResourceEntity();
						md5Entry.setName(MD5ResourceMapper.MD5_PREFIX + resource.getName() + MD5ResourceMapper.MD5_SUFFIX);
						md5Entry.setBytes(md5Base64Bytes);
						filteredResouces.put(md5Entry.getName(), md5Entry);
					}
				}
				filteredResouces.put(resName, resource);
			}
			targetDeployResouces = filteredResouces;
		}
		deployment.setResources(targetDeployResouces);
		if (targetDeployResouces.size() == 0) {
			return deployment;
		}

		deployment.setNew(true);

		// Save the data
		commandContext.getDeploymentEntityManager()
				.insertDeployment(deployment);

		if (commandContext.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_CREATED,
									deployment));
		}

		// Deployment settings
		Map<String, Object> deploymentSettings = new HashMap<String, Object>();
		deploymentSettings.put(
				DeploymentSettings.IS_BPMN20_XSD_VALIDATION_ENABLED,
				deploymentBuilder.isBpmn20XsdValidationEnabled());
		deploymentSettings.put(
				DeploymentSettings.IS_PROCESS_VALIDATION_ENABLED,
				deploymentBuilder.isProcessValidationEnabled());

		// Actually deploy
		commandContext.getProcessEngineConfiguration().getDeploymentManager()
				.deploy(deployment, deploymentSettings);

		if (deploymentBuilder.getProcessDefinitionsActivationDate() != null) {
			scheduleProcessDefinitionActivation(commandContext, deployment);
		}

		if (commandContext.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_INITIALIZED,
									deployment));
		}

		return deployment;
	}

//	private boolean deploymentsDiffer(DeploymentEntity deployment,
//			DeploymentEntity saved) {
//
//		if (deployment.getResources() == null || saved.getResources() == null) {
//			return true;
//		}
//
//		Map<String, ResourceEntity> resources = deployment.getResources();
//		Map<String, ResourceEntity> savedResources = saved.getResources();
//
//		for (String resourceName : resources.keySet()) {
//			ResourceEntity savedResource = savedResources.get(resourceName);
//
//			if (savedResource == null)
//				return true;
//
//			if (!savedResource.isGenerated()) {
//				ResourceEntity resource = resources.get(resourceName);
//
//				byte[] bytes = resource.getBytes();
//				byte[] savedBytes = savedResource.getBytes();
//				if (!Arrays.equals(bytes, savedBytes)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	protected void scheduleProcessDefinitionActivation(
			CommandContext commandContext, DeploymentEntity deployment) {
		for (ProcessDefinitionEntity processDefinitionEntity : deployment
				.getDeployedArtifacts(ProcessDefinitionEntity.class)) {

			// If activation date is set, we first suspend all the process
			// definition
			SuspendProcessDefinitionCmd suspendProcessDefinitionCmd = new SuspendProcessDefinitionCmd(
					processDefinitionEntity, false, null,
					deployment.getTenantId());
			suspendProcessDefinitionCmd.execute(commandContext);

			// And we schedule an activation at the provided date
			ActivateProcessDefinitionCmd activateProcessDefinitionCmd = new ActivateProcessDefinitionCmd(
					processDefinitionEntity, false,
					deploymentBuilder.getProcessDefinitionsActivationDate(),
					deployment.getTenantId());
			activateProcessDefinitionCmd.execute(commandContext);
		}
	}
	
	private boolean isBpmnResource(ResourceEntity resource){
		for (String bpmnSuffix : BpmnDeployer.BPMN_RESOURCE_SUFFIXES) {
			if (resource.getName().toLowerCase().endsWith(bpmnSuffix)) {
				return true;
			}
		}
		return false;
	}
}
