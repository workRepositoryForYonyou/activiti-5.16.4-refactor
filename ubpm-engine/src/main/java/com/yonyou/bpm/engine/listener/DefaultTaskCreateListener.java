package com.yonyou.bpm.engine.listener;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.context.Context;

import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 任务创建监听,任务代理
 * @author zhaohb
 *
 */
public class DefaultTaskCreateListener implements TaskListener,Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		
		addAssignedBy(delegateTask);
		
		String assignee=delegateTask.getAssignee();
		if(assignee==null||"".equals(assignee.trim())){
			return;
		}
		BpmEngineConfiguration bpmEngineConfiguration=(BpmEngineConfiguration)Context.getProcessEngineConfiguration();
		String newAssignee=bpmEngineConfiguration.getAgentService().getAgentUserId(assignee,null);
		if(newAssignee==null||assignee.equals(newAssignee)){
			return;
		}
		delegateTask.setAssignee(newAssignee);
		delegateTask.setOwner(assignee);
		
		delegateTask.addCandidateUser(newAssignee);
		delegateTask.addCandidateUser(assignee);
		
		delegateTask.setVariableLocal("redirectUser", newAssignee);
		
		String message=getClass().getName()+"->任务"+"["+delegateTask.getName()+"]"+"的代理人"+"["+assignee+"]"+"自动转办给了用户"+"["+newAssignee +"]";
		bpmEngineConfiguration.getTaskService().addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), "redirect", message);
		
	}
	private void addAssignedBy(DelegateTask delegateTask){
		   Object assigninfoNeedObj=Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_NEED);
		   
		   Object assigninfoParam=Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_PARAM);
		   
		   if(assigninfoNeedObj!=null&&assigninfoParam!=null){
			   if(((Boolean)assigninfoNeedObj).booleanValue()){
				   String assignedById=ParticipantContext.getTask().getId();
				   delegateTask.setVariableLocal(AssignInfo.ASSIGNINFO_BY, assignedById);
			   }
		   }
	   }
}
