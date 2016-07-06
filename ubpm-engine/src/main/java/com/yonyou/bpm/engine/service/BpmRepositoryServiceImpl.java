package com.yonyou.bpm.engine.service;

import java.util.List;

import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.repository.DeploymentBuilderImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.yonyou.bpm.deploy.MD5FilteredDeployCmd;
import com.yonyou.bpm.engine.cmd.FindNextActivitiesCmd;
import com.yonyou.bpm.engine.cmd.GetProcessDefinitionCmd;
import com.yonyou.bpm.engine.impl.BpmDeploymentQueryImpl;
import com.yonyou.bpm.engine.impl.BpmModelQueryImpl;
import com.yonyou.bpm.engine.impl.BpmProcessDefinitionQueryImpl;
import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmRepositoryService")
public class BpmRepositoryServiceImpl extends RepositoryServiceImpl{
	
	@TracePoint()
	@Override
	public void activateProcessDefinitionById(@TraceValue(TraceKeys.PROCESS_DEFINITION_ID) String processDefinitionId) {
		super.activateProcessDefinitionById(processDefinitionId);
	}
	
	@Override
	public Deployment deploy(DeploymentBuilderImpl deploymentBuilder) {
		return commandExecutor.execute(new MD5FilteredDeployCmd(deploymentBuilder));
	}
	
	/**
	 * 获取流程定义
	 * @param processDefinitionKey
	 * @param processDefinitionId
	 * @param tenantId
	 * @param excludeSuspend
	 * @return
	 */
	@TracePoint
	public ProcessDefinitionEntity getProcessDefinition(@TraceValue(TraceKeys.PROCESS_DEFINITION_KEY)String processDefinitionKey,@TraceValue(TraceKeys.PROCESS_DEFINITION_ID)String processDefinitionId,@TraceValue(TraceKeys.TENANT_ID)String tenantId,boolean excludeSuspend){
		return commandExecutor.execute(new GetProcessDefinitionCmd(processDefinitionKey,processDefinitionId, tenantId,excludeSuspend)); 
	}
	@Override
	public DeploymentQuery createDeploymentQuery(){
		return new BpmDeploymentQueryImpl(commandExecutor);
	}
	 @Override
	public ModelQuery createModelQuery() {
	    return new BpmModelQueryImpl(commandExecutor);
	}
	@Override
	public ProcessDefinitionQuery createProcessDefinitionQuery() {
		 return new BpmProcessDefinitionQueryImpl(commandExecutor);
	}
	/**
	 * 查找活动（activityId）的下一步人工活动
	 * 
	 * @param processDefinitionId
	 * @param activityId
	 * @return
	 */
	@TracePoint
	public List<PvmActivity> findNextActivities(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_ID) String processDefinitionId,
			@TraceValue(TraceKeys.ACTIVITY_ID) String activityId) {
		return commandExecutor.execute(new FindNextActivitiesCmd(
				processDefinitionId, activityId));
	}
}
