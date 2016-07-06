package com.yonyou.bpm.engine.cmd;

import java.util.Date;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmProcessInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignInfoItem;
import com.yonyou.bpm.delegate.BpmTaskListener;
import com.yonyou.bpm.participant.Participant;
/**
 * 流程调整
 * @author zhuyj
 * @author zhaohb
 *
 */
public class JumpToActivityCmd implements Command<ExecutionEntity> {
	protected String processDefinitionId;
	protected String activityId;
	protected String processInstanceId;
	protected String jumpOrigin;
	protected boolean isCreateNewProcesInstance=false;
	protected List<Participant> assignParticipants;
    /**
     * 转到新的流程实例
     * @param processDefinitionId
     * @param activityId
     * @param processInstanceId
     * @param jumpOrigin
     */
    public JumpToActivityCmd(String processDefinitionId, String activityId,String processInstanceId,String jumpOrigin) {
        this(processInstanceId, activityId, jumpOrigin);
        this.processDefinitionId=processDefinitionId;
        isCreateNewProcesInstance=true;
    }
    /**
     * 转到新的流程实例,且指派参与者
     * @param processDefinitionId
     * @param activityId
     * @param processInstanceId
     * @param jumpOrigin
     */
    public JumpToActivityCmd(String processDefinitionId, String activityId,String processInstanceId,String jumpOrigin,List<Participant> assignParticipants) {
        this(processInstanceId, activityId, jumpOrigin);
        this.processDefinitionId=processDefinitionId;
        this.assignParticipants=assignParticipants;
        isCreateNewProcesInstance=true;
    }
    /**
     * 跳转到
     * @param processInstanceId
     * @param activityId
     * @param jumpOrigin
     */
    public JumpToActivityCmd(String processInstanceId,String activityId,
            String jumpOrigin) {
        this.activityId = activityId;
        this.processInstanceId = processInstanceId;
        this.jumpOrigin = jumpOrigin;
        isCreateNewProcesInstance=false;
    }
    /**
     * 跳转到且指派参与者
     * @param processInstanceId
     * @param activityId
     * @param jumpOrigin
     */
    public JumpToActivityCmd(String processInstanceId,String activityId,
            String jumpOrigin,List<Participant> assignParticipants) {
        this.activityId = activityId;
        this.processInstanceId = processInstanceId;
        this.jumpOrigin = jumpOrigin;
        this.assignParticipants=assignParticipants;
        isCreateNewProcesInstance=false;
    }
    public ExecutionEntity execute(CommandContext commandContext) {
    	ExecutionEntity executionEntity=null;
    	//结束所有流程
    	endAllTask(commandContext);
    	if(assignParticipants!=null&&assignParticipants.size()>0){
    		AssignInfo assignInfo=new AssignInfo();
    		AssignInfoItem assignInfoItem=new AssignInfoItem();
    		assignInfoItem.setActivityId(this.activityId);
    		assignInfoItem.setParticipants(assignParticipants.toArray(new Participant[0]));
    		AssignInfoItem[] assignInfoItems=new AssignInfoItem[]{assignInfoItem};
    		assignInfo.setAssignInfoItems(assignInfoItems);
    		commandContext.addAttribute(AssignInfo.ASSIGNINFO_NEED,true);
    		commandContext.addAttribute(AssignInfo.ASSIGNINFO_PARAM, assignInfo);
    	}
    	if(!isCreateNewProcesInstance){
    		jumpToActivityInProcessInstace(commandContext);
    	}else{
    		executionEntity= jumpToActivityCreateProcessInstace(commandContext);
    	}
        return executionEntity;
    }
    /**
     * 跳转到另一个流程定义并创建流程实例
     * @param commandContext
     */
    protected ExecutionEntity jumpToActivityCreateProcessInstace(CommandContext commandContext){
         ProcessInstance processInstance=commandContext.getProcessEngineConfiguration().getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
         if(processInstance==null){
    		 throw new BpmException("找不到流程实例:"+processInstanceId);
         }
         if(processDefinitionId==null||"".equals(processDefinitionId.trim())){
        	 processDefinitionId=processInstance.getProcessDefinitionId();
         }
         ProcessDefinitionEntity processDefinitionEntity = Context
                 .getProcessEngineConfiguration().getDeploymentManager()
                 .findDeployedProcessDefinitionById(processDefinitionId);
         ActivityImpl activity = getActivity(processDefinitionEntity);
         
         ExecutionEntity processInstanceExecutionEntity=(ExecutionEntity)processInstance;
         
         PvmProcessInstance subProcessInstance = processInstanceExecutionEntity.createSubProcessInstance(processDefinitionEntity);
         ExecutionEntity executionEntity=(ExecutionEntity)subProcessInstance;
         ExecutionEntity  executionEntityToExecute=executionEntity.createExecution();
         executionEntityToExecute.setActive(true);
         executionEntityToExecute.setActivity(activity);
         executionEntityToExecute.executeActivity(activity);
         if(activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior)
          	commandContext.getHistoricActivityInstanceEntityManager().deleteHistoricActivityInstancesByProcessInstanceId(processInstanceId);
         
         processInstanceExecutionEntity.setActive(false);
         return executionEntity;
    }
    /**
     * 跳转到本流程实例内的活动
     * @param commandContext
     * @param executionId
     */
    protected ExecutionEntity jumpToActivityInProcessInstace(CommandContext commandContext){
    	ExecutionEntity processIntacnceExutionEntity = commandContext
                .getExecutionEntityManager().findExecutionById(processInstanceId);
    	if(processIntacnceExutionEntity==null){
    		 throw new BpmException("找不到流程实例:"+processInstanceId);
    	}
    	if(processIntacnceExutionEntity.isSuspended()){
    		throw new ActivitiException(getSuspendedTaskException());
    	}
        ProcessDefinitionImpl processDefinition = processIntacnceExutionEntity
                .getProcessDefinition();
        ActivityImpl activity =  getActivity(processDefinition);
        jump(commandContext, processIntacnceExutionEntity, activity);
        
        return processIntacnceExutionEntity;
    }
    /**
     * 跳转到某活动
     * @param commandContext
     * @param executionEntity
     * @param activity
     */
    private void jump(CommandContext commandContext,ExecutionEntity processIntacnceExutionEntity, ActivityImpl activity ){
    	List<Execution>  list= commandContext.getProcessEngineConfiguration().getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).list();
    	if(list==null||list.size()==0){
    		throw new BpmException("流程实例"+processInstanceId+"找不到执行实例！");
    	}
    	ExecutionEntity executionEntityToExecute=null;
    	 //结束非流程实例的执行实例
    	for (Execution execution : list) {
    		ExecutionEntity executionEntity=(ExecutionEntity)execution;
    		if(executionEntity.isActive()){
    			executionEntityToExecute=executionEntity;
    			break;
    		}
		}
    	if(executionEntityToExecute!=null){
    		ExecutionEntity executionEntityToExecute2=executionEntityToExecute.createExecution();
    		executionEntityToExecute2.setActive(true);
    		executionEntityToExecute2.setActivity(activity);
    		executionEntityToExecute2.executeActivity(activity);
    	}else{
    		throw new BpmException("没有运行中的执行实例！");
    	}
    	
        if(activity.getActivityBehavior() instanceof NoneEndEventActivityBehavior)
         	commandContext.getHistoricActivityInstanceEntityManager().deleteHistoricActivityInstancesByProcessInstanceId(processInstanceId);
        executionEntityToExecute.setActive(false);
    }
    /**
     * 结束任务及活动设置endtime
     * @param commandContext
     * @return
     */
    private void  endAllTask(CommandContext commandContext){
    	 String executionId=null;
    	 List<TaskEntity> tasks= commandContext.getTaskEntityManager()
    			 				.findTasksByProcessInstanceId(processInstanceId);
    	 if(tasks==null||tasks.size()==0){
    		 return;
    	 }
    	 if(jumpOrigin==null||"".equals(jumpOrigin)){
    		 jumpOrigin="jumpToActivity";
    		 if(isCreateNewProcesInstance){
    			 jumpOrigin="jumpToActivityNew";
    		 }
    	 }
    	 for (TaskEntity taskEntity : tasks) {
 	        taskEntity.setVariableLocal("跳转原因", jumpOrigin);
 	        commandContext.getTaskEntityManager().deleteTask(taskEntity,
 	                jumpOrigin, false);
 	        executionId=taskEntity.getExecutionId();
 	        taskEntity.fireEvent(BpmTaskListener.EVENTNAME_JUMP);
 	        commandContext.getHistoricTaskInstanceEntityManager().findHistoricTaskInstanceById(taskEntity.getId()).setDeleteReason("jump");
 	        commandContext.getHistoricTaskInstanceEntityManager().findHistoricTaskInstanceById(taskEntity.getId()).setEndTime(new Date());
 	        String activityId=commandContext.getExecutionEntityManager().findExecutionById(executionId).getActivityId();
 	       List<HistoricActivityInstance> historicActivityInstances=commandContext.getProcessEngineConfiguration().getHistoryService().createHistoricActivityInstanceQuery().activityId(activityId).processInstanceId(processInstanceId).list();
 	       if(historicActivityInstances!=null&&historicActivityInstances.size()>0){
 	    	   for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
 	    		  HistoricActivityInstanceEntity historicActivityInstanceEntity=(HistoricActivityInstanceEntity)historicActivityInstance;
 	    		 historicActivityInstanceEntity.setEndTime(commandContext.getProcessEngineConfiguration().getClock().getCurrentTime());
			}
 	       }
         }
    }
    
    private ActivityImpl getActivity(ProcessDefinitionImpl processDefinition){
    	if (processDefinition == null) {
            throw new IllegalArgumentException(
                    "cannot find processDefinition : " + processDefinitionId);
        }
        ActivityImpl activity = processDefinition
                .findActivity(activityId);
        if(activity==null){
       	 throw new IllegalArgumentException(
                    "cannot find activity : " + activityId+"in processDefinition:"+processDefinitionId);
        }
        return activity;
    }
    protected String getSuspendedTaskException() {
        return "流程挂起后，无法调整！";
      }
}
