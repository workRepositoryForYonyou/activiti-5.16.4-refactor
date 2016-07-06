package com.yonyou.bpm.engine.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 查找活动上的参与者
 * @author zhaohb
 *
 */
public class FindParticipantsCmd implements Command<List<String>> {

	private static Logger logger = LoggerFactory
			.getLogger(FindParticipantsCmd.class);
	private String processDefinitionId;
	private String activityId;
	private String executionId;
	//效率优化
	private ProcessDefinitionEntity processDefinitionEntity;
	private ExecutionEntity execution;

	public FindParticipantsCmd(String processDefinitionId, String activityId) {
		this.processDefinitionId = processDefinitionId;
		this.activityId = activityId;
	}
	public FindParticipantsCmd(ProcessDefinitionEntity processDefinitionEntity, String activityId,
			ExecutionEntity execution) {
		this.processDefinitionEntity = processDefinitionEntity;
		this.activityId = activityId;
		this.execution = execution;
	}
	public FindParticipantsCmd(String processDefinitionId, String activityId,
			String executionId) {
		this.processDefinitionId = processDefinitionId;
		this.activityId = activityId;
		this.executionId = executionId;
	}

	@Override
	public List<String> execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl processEngineConfigurationImpl = commandContext
				.getProcessEngineConfiguration();
		if(execution==null){
			if (executionId != null && !"".equals(executionId)) {
				execution = (ExecutionEntity) processEngineConfigurationImpl
						.getRuntimeService().createExecutionQuery()
						.executionId(executionId).singleResult();
				if (processDefinitionId == null || "".equals(processDefinitionId)) {
					ProcessInstance processInstance = processEngineConfigurationImpl
							.getRuntimeService().createProcessInstanceQuery()
							.processInstanceId(execution.getProcessInstanceId())
							.singleResult();
					processDefinitionId = processInstance.getProcessDefinitionId();
				}
			}
		}
		if(processDefinitionEntity==null){
			if (processDefinitionId != null && !"".equals(processDefinitionId)) {
				processDefinitionEntity = Context.getProcessEngineConfiguration()
						.getDeploymentManager()
						.findDeployedProcessDefinitionById(processDefinitionId);
			}
		}

		if (processDefinitionEntity == null) {
			throw new IllegalArgumentException(
					"cannot find processDefinition : " + processDefinitionId);
		}
		ActivityImpl activity = processDefinitionEntity
				.findActivity(activityId);
		if (activity == null) {
			throw new IllegalArgumentException("cannot find activity : "
					+ activityId + "in processDefinition:"
					+ processDefinitionId);
		}
		if (execution == null) {
			execution = new ExecutionEntity(activity);
		}
		ActivityBehavior activityBehavior = activity.getActivityBehavior();
		List<String> participants = new ArrayList<String>();
		if (activityBehavior instanceof MultiInstanceActivityBehavior) {

			MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityBehavior;
			Expression expression = multiInstanceActivityBehavior
					.getCollectionExpression();

			Object obj = expression.getValue(execution);
			if (obj == null)
				return null;
			if (!(obj instanceof Collection)) {
				throw new ActivitiIllegalArgumentException(
						expression.getExpressionText()
								+ "' didn't resolve to a Collection");
			}
			@SuppressWarnings("unchecked")
			Collection<String> collectionObj = (Collection<String>) obj;

			participants.addAll(collectionObj);
		} else if (activityBehavior instanceof UserTaskActivityBehavior) {
			UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
			TaskDefinition taskDefinition = userTaskActivityBehavior
					.getTaskDefinition();
			Set<Expression> candidateUserIdExpressions = taskDefinition
					.getCandidateUserIdExpressions();
			if (candidateUserIdExpressions != null
					&& candidateUserIdExpressions.size() > 0) {
				for (Expression expression : candidateUserIdExpressions) {
					Object obj = expression.getValue(execution);
					if (obj == null)
						continue;
					if (!(obj instanceof Collection)) {
						throw new ActivitiIllegalArgumentException(
								expression.getExpressionText()
										+ "' didn't resolve to a Collection");
					}
					@SuppressWarnings("unchecked")
					Collection<String> collectionObj = (Collection<String>) obj;
					participants.addAll(collectionObj);
				}
			} else {
				logger.info("人工活动（" + activityId + "）的'的候选参与者为空");
			}
		} else {
			logger.info("活动（"
					+ activityId
					+ "）的'"
					+ activityBehavior
					+ "'既不是'MultiInstanceActivityBehavior'也不是'UserTaskActivityBehavior'");
		}
		return participants;
	}

}
