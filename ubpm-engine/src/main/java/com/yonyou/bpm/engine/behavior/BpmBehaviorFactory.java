package com.yonyou.bpm.engine.behavior;

import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.BusinessRuleTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class BpmBehaviorFactory extends DefaultActivityBehaviorFactory{
	@Override
	public BusinessRuleTaskActivityBehavior createBusinessRuleTaskActivityBehavior(
			BusinessRuleTask businessRuleTask) {
	    BusinessRuleTaskActivityBehavior ruleActivity = new BpmBusinessRuleTaskActivityBehavior();
	    
	    for (String ruleVariableInputObject : businessRuleTask.getInputVariables()) {
	      ruleActivity.addRuleVariableInputIdExpression(expressionManager.createExpression(ruleVariableInputObject.trim()));
	    }

	    for (String rule : businessRuleTask.getRuleNames()) {
	      ruleActivity.addRuleIdExpression(expressionManager.createExpression(rule.trim()));
	    }

	    ruleActivity.setExclude(businessRuleTask.isExclude());

	    if (businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
	      ruleActivity.setResultVariable(businessRuleTask.getResultVariableName());
	    } else {
	      ruleActivity.setResultVariable("org.activiti.engine.rules.OUTPUT");
	    }
	    
	    return ruleActivity;
	  }
	@Override
	public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(
		ActivityImpl activity,
		AbstractBpmnActivityBehavior innerActivityBehavior) {
		return new BpmParallelMultiInstanceBehavior(activity, innerActivityBehavior);
	}
	@Override
	public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(
			ActivityImpl activity,
			AbstractBpmnActivityBehavior innerActivityBehavior) {
		 return new BpmSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
	}
}
