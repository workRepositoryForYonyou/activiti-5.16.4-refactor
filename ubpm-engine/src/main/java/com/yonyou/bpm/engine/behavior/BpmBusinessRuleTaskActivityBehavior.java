package com.yonyou.bpm.engine.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.BusinessRuleTaskActivityBehavior;
import org.activiti.engine.impl.pvm.PvmProcessDefinition;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.rules.RulesAgendaFilter;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;

import com.yonyou.bpm.engine.rules.BpmRulesHelper;
/**
 * 商业规则动作重写
 * @author zhaohb
 *
 */
public class BpmBusinessRuleTaskActivityBehavior extends BusinessRuleTaskActivityBehavior{
	@Override
	 public void execute(ActivityExecution execution) throws Exception {
		    PvmProcessDefinition processDefinition = execution.getActivity().getProcessDefinition();
		    String deploymentId = processDefinition.getDeploymentId();
		    
		    KnowledgeBase knowledgeBase = BpmRulesHelper.findKnowledgeBaseByDeploymentId(deploymentId); 
		    StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
		    
		    if (variablesInputExpressions != null) {
		      Iterator<Expression> itVariable = variablesInputExpressions.iterator();
		      while (itVariable.hasNext()) {
		        Expression variable = itVariable.next();
		        ksession.insert(variable.getValue(execution));
		      }
		    }
		    
		    if (!rulesExpressions.isEmpty()) {
		      RulesAgendaFilter filter = new RulesAgendaFilter();
		      Iterator<Expression> itRuleNames = rulesExpressions.iterator();
		      while (itRuleNames.hasNext()) {
		        Expression ruleName = itRuleNames.next();
		        filter.addSuffic(ruleName.getValue(execution).toString());
		      }
		      filter.setAccept(!exclude);
		      ksession.fireAllRules(filter);
		      
		    } else {
		      ksession.fireAllRules();
		    }
		    
		    Collection<Object> ruleOutputObjects = ksession.getObjects();
		    if (ruleOutputObjects != null && !ruleOutputObjects.isEmpty()) {
		      Collection<Object> outputVariables = new ArrayList<Object>();
		      for (Object object : ruleOutputObjects) {
		        outputVariables.add(object);
		      }
		      execution.setVariable(resultVariable, outputVariables);
		    }
		    ksession.dispose();
		    leave(execution);
		  }

}
