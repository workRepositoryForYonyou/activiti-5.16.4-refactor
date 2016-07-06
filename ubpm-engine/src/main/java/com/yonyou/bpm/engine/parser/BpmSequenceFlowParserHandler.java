package com.yonyou.bpm.engine.parser;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.SequenceFlowParseHandler;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.apache.commons.lang3.StringUtils;

import com.yonyou.bpm.utils.AssignInfoUtils;

public class BpmSequenceFlowParserHandler extends SequenceFlowParseHandler {

	@Override
	protected void executeParse(BpmnParse bpmnParse, SequenceFlow sequenceFlow) {

		ScopeImpl scope = bpmnParse.getCurrentScope();

		ActivityImpl sourceActivity = scope.findActivity(sequenceFlow
				.getSourceRef());
		ActivityImpl destinationActivity = scope.findActivity(sequenceFlow
				.getTargetRef());

		TransitionImpl transition = sourceActivity
				.createOutgoingTransition(sequenceFlow.getId());
		bpmnParse.getSequenceFlows().put(sequenceFlow.getId(), transition);
		transition.setProperty("name", sequenceFlow.getName());
		transition
				.setProperty("documentation", sequenceFlow.getDocumentation());
		transition.setDestination(destinationActivity);
		// 条件中默认加入
		boolean isAssignAble = AssignInfoUtils.isAssignAble(destinationActivity);
		String condition = sequenceFlow.getConditionExpression();
		if(isAssignAble){
			if (StringUtils.isNotEmpty(condition)) {
				if (condition.lastIndexOf("}") == condition.length() - 1) {
					condition = condition.substring(0, condition.length() - 1)
							+ "&&bpmBean.assignTarget()}";
				}
	
			} else {
				condition = "${bpmBean.assignTarget()}";
			}
		}
		if (StringUtils.isNotEmpty(condition)) {
			Condition expressionCondition = new UelExpressionCondition(
					bpmnParse.getExpressionManager()
							.createExpression(condition));
			transition.setProperty(PROPERTYNAME_CONDITION_TEXT,
					sequenceFlow.getConditionExpression());
			transition.setProperty(PROPERTYNAME_CONDITION, expressionCondition);
		}

		createExecutionListenersOnTransition(bpmnParse,
				sequenceFlow.getExecutionListeners(), transition);

	}

}
