package com.yonyou.bpm.engine.behavior;

import java.util.Collection;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import com.yonyou.bpm.engine.cmd.CounterSignCmd;

/**
 * 并行
 * 
 * @author zhaohb
 *
 */
public class BpmParallelMultiInstanceBehavior extends
		ParallelMultiInstanceBehavior {

	private static final long serialVersionUID = 1L;

	public BpmParallelMultiInstanceBehavior(ActivityImpl activity,
			AbstractBpmnActivityBehavior originalActivityBehavior) {
		super(activity, originalActivityBehavior);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void signal(ActivityExecution execution, String signalName,
			Object signalData) throws Exception {
		TaskEntity task = (TaskEntity) Context.getCommandContext()
				.getAttribute("currentTask");
		if (task != null) {
			String createType = (String) task
					.getVariableLocal("createType");
			if ("assist".equalsIgnoreCase(createType)) {
				return;
			}
			String parentId = task.getParentTaskId();
			if (parentId != null && !"".equals(parentId.trim())) {
				TaskEntity parentTask = (TaskEntity) Context
						.getProcessEngineConfiguration().getTaskService()
						.createTaskQuery().taskId(parentId).singleResult();
				Boolean counterSigning = (Boolean) parentTask
						.getVariableLocal(CounterSignCmd.COUNTERSIGN_ING);
				if (counterSigning != null && counterSigning.booleanValue()) {
					Collection<String> counterSignCollection = (Collection<String>) execution
							.getVariableLocal("counterSignCollection");
					Integer nrOfCounterSignCompleted = (Integer) execution
							.getVariableLocal("nrOfCounterSignCompleted");

					boolean isLast = counterSignCollection.size() == nrOfCounterSignCompleted + 1;
					execution.setVariableLocal("nrOfCounterSignCompleted",
							nrOfCounterSignCompleted++);
					parentTask.setVariableLocal(CounterSignCmd.COUNTERSIGN_ING, !isLast);
					if (isLast) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Execution {} still active, but multi-instance is completed. Removing this execution.",
									execution);
						}
						execution.inactivate();
						((ExecutionEntity) execution)
								.deleteCascade("multi-instance completed");
					}
					execution.getParent().setActive(isLast);
					return;
				}
			}
		}
		super.signal(execution, signalName, signalData);
	}

	@SuppressWarnings("rawtypes")
	protected int resolveNrOfInstances(ActivityExecution execution) {
		int nrOfInstances = -1;
		if (loopCardinalityExpression != null) {
			nrOfInstances = resolveLoopCardinality(execution);
		} else if (collectionExpression != null) {
			Object obj = collectionExpression.getValue(execution);
			ActivityImpl activityImpl = (ActivityImpl) execution.getActivity();
			if (obj == null||((obj instanceof Collection)&&((Collection)obj).size()==0)) {
				throw new ActivitiObjectNotFoundException( "活动["
						+ activityImpl.getProperty("name") + "("+activityImpl.getId()+")]上找不到用户");
			}
			if (!(obj instanceof Collection)) {
				throw new ActivitiIllegalArgumentException(
						collectionExpression.getExpressionText()
								+ "' didn't resolve to a Collection");
			}
			nrOfInstances = ((Collection) obj).size();
		} else if (collectionVariable != null) {
			Object obj = execution.getVariable(collectionVariable);
			if (obj == null) {
				throw new ActivitiIllegalArgumentException("Variable "
						+ collectionVariable + " is not found");
			}
			if (!(obj instanceof Collection)) {
				throw new ActivitiIllegalArgumentException("Variable "
						+ collectionVariable + "' is not a Collection");
			}
			nrOfInstances = ((Collection) obj).size();
		} else {
			throw new ActivitiIllegalArgumentException(
					"Couldn't resolve collection expression nor variable reference");
		}
		return nrOfInstances;
	}

}
