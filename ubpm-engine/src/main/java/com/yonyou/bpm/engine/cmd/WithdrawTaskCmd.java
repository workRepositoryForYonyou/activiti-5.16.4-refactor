package com.yonyou.bpm.engine.cmd;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.bpmn.behavior.GatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntityManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.delegate.BpmTaskListener;
import com.yonyou.bpm.engine.behavior.BpmParallelMultiInstanceBehavior;
import com.yonyou.bpm.engine.behavior.BpmSequentialMultiInstanceBehavior;

/**
 * 撤销任务.
 */
public class WithdrawTaskCmd implements Command<Integer> {
    private static Logger logger = LoggerFactory
            .getLogger(WithdrawTaskCmd.class);
    private String historyTaskId;
    private ActivityImpl currentActivity;

    /**
     * 这个historyTaskId是已经完成的一个任务的id.
     */
    public WithdrawTaskCmd(String historyTaskId) {
        this.historyTaskId = historyTaskId;
    }

    /**
     * 撤销流程.
     * 
     * @return 0-撤销成功 1-流程结束 2-下一结点已经通过或当前任务未执行,不能撤销
     */
    public Integer execute(CommandContext commandContext) {
        // 获得历史任务
        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
        //当前任务未执行
        if(historicTaskInstanceEntity==null||historicTaskInstanceEntity.getEndTime()==null)
        	throw new ActivitiException("当前活动未执行,不能撤回!");
        else if(historicTaskInstanceEntity.getDeleteReason()!=null&&historicTaskInstanceEntity.getDeleteReason().equals("deleted"))
        	throw new ActivitiException("当前活动未执行,不能撤回!");
        
        if(historicTaskInstanceEntity.getDeleteReason()!=null&&historicTaskInstanceEntity.getDeleteReason().equals("jump"))
        	throw new ActivitiException("当前活动已跳转,不能撤回!");
        
        ExecutionEntity processIntacnceExutionEntity = Context
                .getCommandContext().getExecutionEntityManager().findExecutionById(historicTaskInstanceEntity.getProcessInstanceId());
        if(processIntacnceExutionEntity!=null&&processIntacnceExutionEntity.isSuspended()){
    		throw new ActivitiException("流程实例已挂起,不能撤回!");
    	}
        if(historicTaskInstanceEntity.getDeleteReason().equals("withdraw"))
        	throw new ActivitiException("该活动为已撤销活动,不能撤回!");
        HistoricProcessInstanceEntity historicProcessInstanceEntity=Context
        .getCommandContext().getHistoricProcessInstanceEntityManager()
        .findHistoricProcessInstance(historicTaskInstanceEntity.getProcessInstanceId());
        if(historicProcessInstanceEntity.getEndTime()!=null)
        	throw new ActivitiException("流程实例已结束,不能撤回!");

        // 获得历史节点
        HistoricActivityInstanceEntity historicActivityInstanceEntity = getHistoricActivityInstanceEntity(historyTaskId);
        
        ProcessDefinitionEntity processDefEngity = (ProcessDefinitionEntity) Context.getCommandContext().getProcessEngineConfiguration().getRepositoryService().getProcessDefinition(historicActivityInstanceEntity.getProcessDefinitionId());
        currentActivity = processDefEngity.findActivity(historicActivityInstanceEntity.getActivityId());
        if(currentActivity==null||!checkCouldWithdraw(currentActivity, historicActivityInstanceEntity.getProcessInstanceId())) {
            logger.info("cannot withdraw {}", historyTaskId);
            throw new ActivitiException("下一个活动已经完成,不能撤回!");
        }

        // 删除所有活动中的task
       // this.deleteActiveTasks(historicTaskInstanceEntity
        //        .getProcessInstanceId());

        // 获得期望撤销的节点后面的所有节点历史
        List<String> historyNodeIds = new ArrayList<String>();
 
        //收集当前活动的后续节点
        this.collectNodes(currentActivity, historyNodeIds);
        
        this.deleteHistoryActivities(historyNodeIds, historicActivityInstanceEntity.getProcessInstanceId());
        // 恢复期望撤销的任务和历史
        this.processHistoryTask(historicTaskInstanceEntity,
                historicActivityInstanceEntity);

        logger.info("activiti is withdraw {}",
                historicTaskInstanceEntity.getName());

        return 0;
    }

    public HistoricActivityInstanceEntity getHistoricActivityInstanceEntity(
            String historyTaskId) {
        logger.info("historyTaskId : {}", historyTaskId);
        
        Map<String, Object> parameterMap = new HashMap<String, Object>();
    	parameterMap.put("sql", "select * from ACT_HI_ACTINST where task_id_='" + historyTaskId + "'");
    	List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
    	if(historicActivityInstance!=null && historicActivityInstance.size() > 0)
        	return (HistoricActivityInstanceEntity) historicActivityInstance.get(0);
        else
        	return null;
    }

       
    public boolean checkCouldWithdraw(ActivityImpl activity, String processInstanceId) {
    		for (PvmTransition transition : activity.getOutgoingTransitions()) {
            ActivityImpl dest = (ActivityImpl) transition.getDestination();
            ActivityBehavior activityBehavior = dest.getActivityBehavior();
            if(activityBehavior instanceof UserTaskActivityBehavior||activityBehavior instanceof MultiInstanceActivityBehavior && !dest.getId().equals(currentActivity.getId())){
            	Map<String, Object> parameter = new HashMap<String, Object>();
            	parameter.put("sql", "select * from ACT_HI_ACTINST where act_id_='" + dest.getId() + "' and proc_inst_id_='" + processInstanceId + "'");
                List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameter, 0, 100);
            	//HistoricActivityInstanceEntity activityInstanceEntity = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(dest.getId(), processInstanceId);
            	for(int i=0;i<historicActivityInstance.size();i++)
            	{
	                if(historicActivityInstance.get(i)!=null)
	            	{
		            	Map<String, Object> parameterMap = new HashMap<String, Object>();
		            	parameterMap.put("sql", "select * from ACT_HI_TASKINST where proc_inst_id_='" + historicActivityInstance.get(i).getProcessInstanceId() + "' and task_def_key_='" + historicActivityInstance.get(i).getActivityId() + "'");
		            	
		            	List<HistoricTaskInstance> historyTaskInstances = Context.getCommandContext().getHistoricTaskInstanceEntityManager().findHistoricTaskInstancesByNativeQuery(parameterMap, 0, 100);
		             	for (HistoricTaskInstance taskInstane : historyTaskInstances) {
		             		if(taskInstane!=null&&taskInstane.getEndTime() != null&&!taskInstane.getDeleteReason().equals("withdraw")&&!taskInstane.getDeleteReason().equals("deleted")&&!taskInstane.getDeleteReason().equals("Delete"))
		            			return false;
						}
	            	}
            	}
            }
            else if(activityBehavior instanceof GatewayActivityBehavior){
            	return checkCouldWithdraw(dest, processInstanceId);
            }
            else if(activityBehavior instanceof NoneEndEventActivityBehavior || dest.getId().equals(currentActivity.getId())){
            	continue;
            }
            else {
            	return false;
            }
        }
        return true;
    }


    /**
     * 删除未完成任务.
     */
    public void deleteActiveTasks(String processInstanceId) {
    	Context.getCommandContext().getTaskEntityManager()
        .deleteTasksByProcessInstanceId(processInstanceId, null, true);
    }

       
    /**
     * 查找当前活动的下一级节点
     * @param currentActivity
     * @param historyNodeIds
     */
    public void collectNodes(ActivityImpl currentActivity, List<String> historyNodeIds) {
    	 List<PvmTransition> transitions = currentActivity.getOutgoingTransitions();
         for (PvmTransition pvmTransiton : transitions) {
	      	 ActivityImpl dest = (ActivityImpl) pvmTransiton.getDestination();
	      	 ActivityBehavior activityBehavior = dest.getActivityBehavior();
	      	 if(activityBehavior instanceof GatewayActivityBehavior){
	      		collectNodes(dest,historyNodeIds);
	      	 }
	      	 else
	      	   historyNodeIds.add(dest.getId());
         }
    }

    /**
     * 删除历史节点.
     */
    public void deleteHistoryActivities(List<String> historyNodeIds, String processInstanceId) {
    	for (int i = 0; i < historyNodeIds.size(); i++) {
    		 Map<String, Object> parameter = new HashMap<String, Object>();
         	 parameter.put("sql", "select * from ACT_HI_ACTINST where act_id_='" + historyNodeIds.get(i) + "' and proc_inst_id_='" + processInstanceId + "'");
         	 List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameter, 0, 100);
        	for(int j=0;j<historicActivityInstance.size();j++)
         	{
         		HistoricActivityInstanceEntity historicActivityInstanceEntity=(HistoricActivityInstanceEntity) historicActivityInstance.get(j);
         		 if(historicActivityInstanceEntity.getActivityType().indexOf("Task")!=-1)
        		 {
    	    		 TaskEntity task = Context
    	    			      .getCommandContext()
    	    			      .getTaskEntityManager()
    	    			      .findTaskById(historicActivityInstanceEntity.getTaskId());
    	    		 if(task!=null)
    	    		 {
    	    			 task.getExecution().setActive(false);
    	    			 task.getExecution().deleteVariablesInstanceForLeavingScope();
    	    			 task.getExecution().setDeleteReason("withdraw");
    	    			 if(historicActivityInstanceEntity!=null&&historicActivityInstanceEntity.getTaskId()!=null)
    	    				 Context.getCommandContext().getTaskEntityManager().deleteTask(task, "withdraw", false);
    	    			 task.getExecution().remove();
    	    			 task.fireEvent(BpmTaskListener.EVENTNAME_WITHDRAW);
    	    			 //String taskId = task.getId();
    	    			 //if (taskId != null) {
        	    		//	 historicTaskManager.deleteHistoricTaskInstanceById(taskId);
        	    		// }
    	    		 }
        		 }
        	}
        	if(historicActivityInstance.size()>0)
        	{
        	 List<ExecutionEntity> executionEntityList=Context.getCommandContext().getExecutionEntityManager().findChildExecutionsByParentExecutionId(historicActivityInstance.get(0).getExecutionId());
    		 for(int k=0;k<executionEntityList.size();k++)
    		 {
    			if(!(executionEntityList.get(k).getProcessInstanceId().equals(executionEntityList.get(k).getId())))
    			 {
    				 executionEntityList.get(k).inactivate();
    				 executionEntityList.get(k).deleteCascade("withdraw");
    			 }
    			if(k==executionEntityList.size()-1)
    			{
        			executionEntityList.get(k).getParent().destroy();
	       			executionEntityList.get(k).getParent().remove();
    			}
    		 }
        	}
		}
    }

    public void processHistoryTask(
            HistoricTaskInstanceEntity historicTaskInstanceEntity,
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
    	
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(historicActivityInstanceEntity.getExecutionId());
        if(executionEntity!=null)
        {
        	if(!executionEntity.isActive())
        		executionEntity.setActive(true);
        	executionEntity.setCachedEntityState(7);
        	//executionEntity.setDeleteReason(null);
        	ActivityImpl activityImpl=getActivity(historicActivityInstanceEntity);
        	executionEntity.setActivity(activityImpl);
        	List<VariableInstanceEntity> variableList=Context.getCommandContext().getVariableInstanceEntityManager().findVariableInstancesByExecutionId(executionEntity.getId());
        	ExecutionEntity currentExecutionEntity =executionEntity;
        	if((variableList==null||variableList.size()==0)&&executionEntity.getParentId()!=null)
        	{
        		currentExecutionEntity=Context.getCommandContext()
                        .getExecutionEntityManager()
                        .findExecutionById(executionEntity.getParentId());
        		variableList=Context.getCommandContext().getVariableInstanceEntityManager().findVariableInstancesByExecutionId(executionEntity.getParentId());
        	}
        	VariableInstanceEntity completedVariable=null;
        	VariableInstanceEntity activeVariable=null;
        	VariableInstanceEntity loopCounter=null;
        	if(variableList.size()>0&&executionEntity.getParentId()!=null)
        	{
	        	for(int i=0;i<variableList.size();i++)
	        	{
	        		if(variableList.get(i).getName().equals("nrOfCompletedInstances"))
	        			completedVariable=variableList.get(i);
	        		else if(variableList.get(i).getName().equals("nrOfActiveInstances"))
	        			activeVariable=variableList.get(i);
	        		else if(variableList.get(i).getName().equals("loopCounter"))
	        			loopCounter=variableList.get(i);
	        	}
	        	if(currentExecutionEntity!=null&&completedVariable!=null&&completedVariable.getValue()!=null&&(Integer) completedVariable.getValue()>=1)
        		{
	        		currentExecutionEntity.setVariableLocal("nrOfCompletedInstances", (Integer) completedVariable.getValue()-1);
        		}
	        	if(currentExecutionEntity!=null&&activeVariable!=null&&activeVariable.getValue()!=null)
	        	{
	        		if(!(activityImpl.getActivityBehavior() instanceof BpmSequentialMultiInstanceBehavior))
	        			currentExecutionEntity.setVariableLocal("nrOfActiveInstances", (Integer) activeVariable.getValue()+1);
	        	}	
	        	if(currentExecutionEntity!=null&&loopCounter!=null&&loopCounter.getValue()!=null)
	        	{
	        		currentExecutionEntity.setVariableLocal("loopCounter", (Integer) loopCounter.getValue()-1);
	        	}	
        	}
        	if(activityImpl.getActivityBehavior() instanceof BpmSequentialMultiInstanceBehavior)
            {
            	Map<String, Object> parameter = new HashMap<String, Object>();
            	parameter.put("sql", "select * from ACT_RU_TASK where TASK_DEF_KEY_='" + historicTaskInstanceEntity.getTaskDefinitionKey() + "' and proc_inst_id_='" + historicActivityInstanceEntity.getProcessInstanceId() + "'");
            	List<Task> taskList=Context.getCommandContext().getTaskEntityManager().findTasksByNativeQuery(parameter, 0, 100);
            	Context.getCommandContext().getTaskEntityManager().deleteTask((TaskEntity) taskList.get(0), "withdraw", false);
            }
        }
        List<HistoricActivityInstance> historicActivityInstanceEntityList =null;
     	String newExecutionId=null;
        //多实例撤回
     	ActivityImpl activityImpl=getActivity(historicActivityInstanceEntity);
        if(executionEntity==null&&activityImpl.getActivityBehavior() instanceof BpmParallelMultiInstanceBehavior)
        {
        	int completedInstances=0;
        	int activeInstances=0;
        	int allInstances=0;
        	Map<String, Object> parameter = new HashMap<String, Object>();
        	parameter.put("sql", "select * from ACT_HI_ACTINST where act_id_='" + historicActivityInstanceEntity.getActivityId() + "' and proc_inst_id_='" + historicActivityInstanceEntity.getProcessInstanceId() + "'");
            historicActivityInstanceEntityList = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameter, 0, 100);
            if(historicActivityInstanceEntityList!=null)	
            	allInstances=historicActivityInstanceEntityList.size();
			ExecutionEntity parentExecution=new ExecutionEntity();
			parentExecution.setActive(false);
			parentExecution.setConcurrent(false);
			parentExecution.setScope(true);
			parentExecution.setSuspensionState(1);
			String parentId=historicActivityInstanceEntityList.get(0).getExecutionId()+Math.round(Math.random()*1000000);
			parentExecution.setId(parentId);
			parentExecution.setParentId(historicActivityInstanceEntityList.get(0).getProcessInstanceId());
			parentExecution.setProcessDefinitionId(historicActivityInstanceEntity.getProcessDefinitionId());
			ExecutionEntity processIntacnceExutionEntity = Context.getCommandContext()
                    .getExecutionEntityManager().findExecutionById(historicTaskInstanceEntity.getProcessInstanceId());
			parentExecution.setProcessInstance(processIntacnceExutionEntity);
			parentExecution.setActivity(getActivity(historicActivityInstanceEntity));
			Context.getCommandContext().getExecutionEntityManager().insert(parentExecution);
        	Map<String, Object> parameterMap = new HashMap<String, Object>();
        	parameterMap.put("sql", "select * from ACT_HI_TASKINST where proc_inst_id_='" + ((HistoricActivityInstanceEntity)historicActivityInstanceEntityList.get(0)).getProcessInstanceId() 
        			+ "' and task_def_key_='" + ((HistoricActivityInstanceEntity)historicActivityInstanceEntityList.get(0)).getActivityId() + "'");
        	List<HistoricTaskInstance> historyTaskInstances = Context.getCommandContext().getHistoricTaskInstanceEntityManager().findHistoricTaskInstancesByNativeQuery(parameterMap, 0, 100);
         	for (HistoricTaskInstance taskInstane : historyTaskInstances) {
        		if(taskInstane!=null&&taskInstane.getEndTime() != null&&taskInstane.getDeleteReason().equals("completed"))
        			completedInstances++;
        		else
        			activeInstances++;
			}
            for(int i=0;i<historicActivityInstanceEntityList.size();i++)
        	{
        		if(!(historicActivityInstanceEntityList.get(i).getId().equals(historicActivityInstanceEntity.getId())))
    			{
        			ExecutionEntity tempExecution=new ExecutionEntity();
        			tempExecution.setDeleteReason(((HistoricActivityInstanceEntity)historicActivityInstanceEntityList.get(i)).getDeleteReason());
        			tempExecution.setActive(false);
        			tempExecution.setConcurrent(false);
        			tempExecution.setScope(false);
        			tempExecution.setParentId(parentId);
        			tempExecution.setProcessDefinitionId(historicActivityInstanceEntity.getProcessDefinitionId());
        			tempExecution.setId(historicActivityInstanceEntityList.get(i).getExecutionId());
        			tempExecution.setProcessInstance(processIntacnceExutionEntity);
        			tempExecution.setActivity(getActivity(historicActivityInstanceEntity));
        			Context.getCommandContext().getExecutionEntityManager().insert(tempExecution);
    			}
    			else
    			{
    				//newExecutionId=historicActivityInstanceEntityList.get(i).getExecutionId()+Math.round(Math.random()*1000000);
    				ExecutionEntity tempExecution=new ExecutionEntity();
        			tempExecution.setActive(true);
        			tempExecution.setConcurrent(true);
        			tempExecution.setScope(false);
        			tempExecution.setProcessDefinitionId(historicActivityInstanceEntity.getProcessDefinitionId());
        			historicActivityInstanceEntity.setDeleteReason("withdraw");
        			tempExecution.setParentId(parentId);
        			tempExecution.setId(historicActivityInstanceEntityList.get(i).getExecutionId());
        			tempExecution.setProcessInstance(processIntacnceExutionEntity);
        			tempExecution.setActivity(getActivity(historicActivityInstanceEntity));
        			Context.getCommandContext().getExecutionEntityManager().insert(tempExecution);
                 	completedInstances--;
                 	activeInstances++;
    			}
        			
        	}
            parentExecution.setVariableLocal("nrOfInstances", allInstances);
            parentExecution.setVariableLocal("nrOfCompletedInstances", completedInstances);
            parentExecution.setVariableLocal("nrOfActiveInstances", activeInstances);
        }
        
        if(executionEntity==null&&activityImpl.getActivityBehavior() instanceof BpmSequentialMultiInstanceBehavior)
        {
        	ExecutionEntity execution=new ExecutionEntity();
			execution.setActive(true);
			execution.setConcurrent(false);
			execution.setScope(true);
			execution.setSuspensionState(1);
			String parentId=historicActivityInstanceEntity.getExecutionId();
			execution.setId(parentId);
			execution.setParentId(historicActivityInstanceEntity.getProcessInstanceId());
			execution.setProcessDefinitionId(historicActivityInstanceEntity.getProcessDefinitionId());
			ExecutionEntity processIntacnceExutionEntity = Context.getCommandContext()
                    .getExecutionEntityManager().findExecutionById(historicTaskInstanceEntity.getProcessInstanceId());
			execution.setProcessInstance(processIntacnceExutionEntity);
			execution.setActivity(getActivity(historicActivityInstanceEntity));
			Context.getCommandContext().getExecutionEntityManager().insert(execution);
			Map<String, Object> parameter = new HashMap<String, Object>();
        	parameter.put("sql", "select * from ACT_HI_ACTINST where act_id_='" + historicActivityInstanceEntity.getActivityId() + "' and proc_inst_id_='" + historicActivityInstanceEntity.getProcessInstanceId() + "'");
            historicActivityInstanceEntityList = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameter, 0, 100);
			//设置nrOfInstances的值，暂时不知道从哪里取
            //execution.setVariableLocal("nrOfInstances", historicActivityInstanceEntityList.size());
			execution.setVariableLocal("nrOfCompletedInstances", historicActivityInstanceEntityList.size()-1);
			execution.setVariableLocal("nrOfActiveInstances", 1);
			execution.setVariableLocal("loopCounter", historicActivityInstanceEntityList.size()-1);
        	
        }
    	
        historicTaskInstanceEntity.setEndTime(null);
        historicTaskInstanceEntity.setDurationInMillis(null);
        historicActivityInstanceEntity.setEndTime(null);
        historicTaskInstanceEntity.setDeleteReason(null);
        historicActivityInstanceEntity.setDurationInMillis(null);

        TaskEntity task = TaskEntity.create(new Date());
        task.setProcessDefinitionId(historicTaskInstanceEntity
                .getProcessDefinitionId());
        task.setId(historicTaskInstanceEntity.getId());
        task.setAssigneeWithoutCascade(historicTaskInstanceEntity.getAssignee());
        task.setParentTaskIdWithoutCascade(historicTaskInstanceEntity
                .getParentTaskId());
        task.setNameWithoutCascade(historicTaskInstanceEntity.getName());
        if(executionEntity!=null)
        {
        	ExecutionEntity hisExecution=Context.getCommandContext()
            .getExecutionEntityManager()
            .findExecutionById(historicTaskInstanceEntity.getExecutionId());
        	if(hisExecution!=null)
        		task.setExecutionId(historicTaskInstanceEntity.getExecutionId());
        	else
        		task.setExecutionId(executionEntity.getId());
        }
        else
        {
        	task.setExecutionId(historicActivityInstanceEntity.getExecutionId());
        }
        task.setTaskDefinitionKey(historicTaskInstanceEntity
                .getTaskDefinitionKey());
        task.setPriority(historicTaskInstanceEntity.getPriority());
        task.setProcessInstanceId(historicTaskInstanceEntity
                .getProcessInstanceId());
        task.setDescriptionWithoutCascade(historicTaskInstanceEntity
                .getDescription());
        task.setFormKey(historicTaskInstanceEntity.getFormKey());

        Context.getCommandContext().getTaskEntityManager().insert(task);

        task.fireEvent(BpmTaskListener.EVENTNAME_WITHDRAW);
    }

    public ActivityImpl getActivity(
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                historicActivityInstanceEntity.getProcessDefinitionId())
                .execute(Context.getCommandContext());

        return processDefinitionEntity
                .findActivity(historicActivityInstanceEntity.getActivityId());
    }
}
