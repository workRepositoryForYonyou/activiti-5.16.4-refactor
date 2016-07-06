package com.yonyou.bpm.utils;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

/**
 * 多实例工具类
 * 
 * @author zhaohb
 *
 */
public class MultiInstanceUtils {
	// Variable names for outer instance(as described in spec)
	
	public static final String TYPE_PARALLEL = "parallel";
	public static final String NUMBER_OF_INSTANCES = "nrOfInstances";
	public static final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
	public static final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
	/**
	 * 获取循环相关变量
	 * @param execution
	 * @param variableName
	 * @return
	 */
	public static Integer getLoopVariable(ActivityExecution execution,
			String variableName) {
		Object value = execution.getVariableLocal(variableName);
		ActivityExecution parent = execution.getParent();
		while (value == null && parent != null) {
			value = parent.getVariableLocal(variableName);
			parent = parent.getParent();
		}
		return (Integer) (value != null ? value : 0);
	}
	/**
	 * 完成任务之前预判是否满足完成活动的条件
	 * @param multiInstanceActivityBehavior
	 * @param execution
	 * @return
	 */
	public static boolean completionConditionSatisfiedBeforeComplete(
			MultiInstanceActivityBehavior multiInstanceActivityBehavior,
			ActivityExecution execution) {
		Expression completionConditionExpression=multiInstanceActivityBehavior.getCompletionConditionExpression();
		if (completionConditionExpression != null) {
			String originalExpressionText=completionConditionExpression.getExpressionText();
			String resultExpressionText=originalExpressionText;
			if(originalExpressionText.indexOf(NUMBER_OF_COMPLETED_INSTANCES)!=-1){
				resultExpressionText=resultExpressionText.replaceAll(NUMBER_OF_COMPLETED_INSTANCES, "("+NUMBER_OF_COMPLETED_INSTANCES+"+1)");
			}
			if(originalExpressionText.indexOf(NUMBER_OF_ACTIVE_INSTANCES)!=-1){
				resultExpressionText=resultExpressionText.replaceAll(NUMBER_OF_ACTIVE_INSTANCES, "("+NUMBER_OF_ACTIVE_INSTANCES+"-1)");
			}
			completionConditionExpression=Context.getCommandContext().getProcessEngineConfiguration().getBpmnParser().getExpressionManager().createExpression(resultExpressionText);
			Object value = completionConditionExpression.getValue(execution);
			if (!(value instanceof Boolean)) {
				throw new ActivitiIllegalArgumentException(
						"completionCondition '"
								+ originalExpressionText
								+ "' does not evaluate to a boolean value");
			}
			Boolean booleanValue = (Boolean) value;
			return booleanValue;
		}
		return false;
	}
}
