package com.yonyou.bpm.engine.cmd;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.GatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignInfoItem;
import com.yonyou.bpm.engine.behavior.BpmParallelMultiInstanceBehavior;
import com.yonyou.bpm.engine.behavior.BpmSequentialMultiInstanceBehavior;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.utils.TaskUtils;

/**
 * 退回command
 * 需求：由A环节的张三  流转到  B环节的李四； 这时，李四退回的话，则直接生成新的任务而且这个任务的执行人是张三，环节B的任务中，李四任务的状态为退回，并且接收人显示为“张三”；  
 * @author wanghaitao
 *
 */
public class RetreatCmd implements Command<Task>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public String currentTask;
	
	public RetreatCmd(String currentTask) {
		this.currentTask = currentTask;
	}

	@Override
	public Task execute(CommandContext commandContext) {
		TaskEntity taskEntity=(TaskEntity)commandContext.getProcessEngineConfiguration().getTaskService().createTaskQuery().taskId(currentTask).singleResult();
		if(taskEntity==null){
			throw new BpmException("找不到正在执行的任务："+this.currentTask);
		}
		ExecutionEntity currentExecution = taskEntity.getExecution();
		ExecutionEntity parentExecution = null;
		List<PvmTransition> currentTransition = currentExecution.getActivity().getIncomingTransitions();
		if(currentExecution == null) throw new BpmException("无法撤回");
		String assignee = null;
		HistoricActivityInstanceEntity acitvityInstance = null;
		List<HistoricActivityInstance> historicActivityInstanceEntityList =null;
		ActivityImpl activityImpl=null;
		List<HistoricTaskInstance> taskList = commandContext.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery()
				.executionId(currentExecution.getId()).orderByHistoricTaskInstanceEndTime().finished().desc().list();
		HistoricTaskInstance lastTask = null;
		//对于多实例的审批任务暂时先直接退回到上一环节
		if(taskList != null && taskList.size() > 0){
			lastTask = taskList.get(0);
			if(currentExecution.getParent()!=null){
				parentExecution = currentExecution.getParent();
			}
			Map<String, Object> parameterMap = new HashMap<String, Object>();
	    	parameterMap.put("sql", "select * from ACT_HI_ACTINST where proc_inst_id_='" + parentExecution.getId() + "' AND END_TIME_ is not null order by END_TIME_ DESC");
	    	List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
	    	if(historicActivityInstance!=null && historicActivityInstance.size() > 0){
	    		for (int i = 0; i < historicActivityInstance.size(); i++) {
	    			HistoricActivityInstance activityIns = historicActivityInstance.get(i);
	    			if("inclusiveGateway".equals(activityIns.getActivityType()))
	    				continue;
	    			else{
		    			acitvityInstance = (HistoricActivityInstanceEntity) activityIns;
		    			boolean flag = findTransition(currentTransition,activityIns,taskEntity);
		    			if(flag) break;
	    			}
				}
				String taskId = acitvityInstance.getTaskId();
				lastTask = commandContext.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
				assignee = lastTask.getAssignee();
				activityImpl = getActivity(acitvityInstance);
	    	}
		}
		else{
			if(currentExecution.getParent()!=null){
				parentExecution = currentExecution.getParent();
			}
			Map<String, Object> parameterMap = new HashMap<String, Object>();
	    	parameterMap.put("sql", "select * from ACT_HI_ACTINST where proc_inst_id_='" + parentExecution.getId() + "' AND END_TIME_ is not null order by END_TIME_ DESC ");
	    	List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
	    	if(historicActivityInstance!=null && historicActivityInstance.size() > 0){
	    		for (int i = 0; i < historicActivityInstance.size(); i++) {
	    			HistoricActivityInstance activityIns = historicActivityInstance.get(i);
	    			if("inclusiveGateway".equals(activityIns.getActivityType()))
	    				continue;
	    			else{
		    			acitvityInstance = (HistoricActivityInstanceEntity) activityIns;
		    			boolean flag = findTransition(currentTransition,activityIns,taskEntity);
		    			if(flag) break;
	    			}
				}
				String taskId = acitvityInstance.getTaskId();
				lastTask = commandContext.getProcessEngineConfiguration().getHistoryService().createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
				assignee = lastTask.getAssignee();
				activityImpl = getActivity(acitvityInstance);
	    	}
		}
		//串行模式
		if(activityImpl.getActivityBehavior() instanceof BpmSequentialMultiInstanceBehavior)
        {
			BpmSequentialMultiInstanceBehavior behavior = (BpmSequentialMultiInstanceBehavior)activityImpl.getActivityBehavior();
			Expression expression = behavior.getCollectionExpression();
			ExecutionEntity newExecution = parentExecution.createExecution();
			newExecution.setActive(true);
			newExecution.setConcurrent(false);
			newExecution.setScope(true);
			newExecution.setSuspensionState(1);
			newExecution.setParentId(parentExecution.getId());
			newExecution.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
			ExecutionEntity processIntacnceExutionEntity = Context.getCommandContext().getExecutionEntityManager().findExecutionById(acitvityInstance.getProcessInstanceId());
			newExecution.setProcessInstance(processIntacnceExutionEntity);
			newExecution.setActivity(activityImpl);
			Map<String, Object> parameter = new HashMap<String, Object>();
        	parameter.put("sql", "select * from ACT_HI_ACTINST where act_id_='" + acitvityInstance.getActivityId() + "' and proc_inst_id_='" + acitvityInstance.getProcessInstanceId() + "'");
            historicActivityInstanceEntityList = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameter, 0, 100);
			//设置nrOfInstances的值
//            Object obj = expression.getValue(newExecution); 
//            Collection nrOfInstances = null;
//            if(obj instanceof Collection){
//            	nrOfInstances = (Collection) obj;
//            }
//            AssignInfoItem assignInfoItem = new AssignInfoItem();
//            Participant[] participants = new Participant[3];
//            for(int i=0;i<3;i++){
//            	Participant par = new Participant();
//                par.setId("11111111111111"+i);
//                par.setType("USER");
//                participants[i] = par;
//            }
//            assignInfoItem.setParticipants(participants);
//            assignInfoItem.setActivityId(activityImpl.getId());
//            AssignInfoItem[] assignInfoItems = new AssignInfoItem[1];
//            assignInfoItems[0] = assignInfoItem;
//            //将审批历史作为指派信息
//            AssignInfo assignInfo = new AssignInfo();
//            assignInfo.setTaskId(currentTask);
//            assignInfo.setAssignInfoItems(assignInfoItems);
//			commandContext.addAttribute(AssignInfo.ASSIGNINFO_NEED,true);
//			commandContext.addAttribute(AssignInfo.ASSIGNINFO_PARAM, assignInfo);
            newExecution.setVariableLocal("nrOfInstances", 1);
			newExecution.setVariableLocal("nrOfCompletedInstances", 0);
			newExecution.setVariableLocal("nrOfActiveInstances", 1);
			newExecution.setVariableLocal("loopCounter", 0);
			//创建新的代办任务
			TaskEntity retreatTaskEntity = TaskEntity.create(new Date());
			retreatTaskEntity.setProcessDefinitionId(taskEntity.getExecution().getProcessDefinitionId());
			retreatTaskEntity.setAssignee(assignee);;
			retreatTaskEntity.setName(lastTask.getName());
			retreatTaskEntity.setExecutionId(newExecution.getId());
			retreatTaskEntity.setTaskDefinitionKey(lastTask.getTaskDefinitionKey());
			retreatTaskEntity.setProcessInstanceId(lastTask.getProcessInstanceId());
			retreatTaskEntity.setFormKey(lastTask.getFormKey());
	        retreatTaskEntity=TaskUtils.createAndInsert(newExecution, retreatTaskEntity,assignee);
	        retreatTaskEntity.getExecution().setActive(true);
	     	//更新现在的任务状态为退回
	        taskEntity.getExecution().setDeleteReason("retreat");
			commandContext.getTaskEntityManager().deleteTask(taskEntity, "retreat", false);
			//记录创建的任务
	        Context.getCommandContext().getHistoryManager().recordActivityStart(retreatTaskEntity.getExecution());
	        Context.getCommandContext().getHistoryManager().recordTaskAssignment(taskEntity);
	        if (Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
	            Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	              ActivitiEventBuilder.createEntityEvent(ActivitiEventType.TASK_CREATED, retreatTaskEntity));
	        }
	        retreatTaskEntity.fireEvent(TaskListener.EVENTNAME_CREATE);
	        currentExecution.remove();
			//查找当前任务关联的活动id是否完成。
			Map<String, Object> parameterMap = new HashMap<String, Object>();
	    	parameterMap.put("sql", "select * from ACT_HI_ACTINST where task_id_='" + taskEntity.getId() + "'");
	    	List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
	    	if(historicActivityInstance!=null && historicActivityInstance.size() > 0){
				HistoricActivityInstanceEntity activityinstance = (HistoricActivityInstanceEntity) historicActivityInstance.get(0);
				activityinstance.setDeleteReason("retreat");
				activityinstance.setEndTime(new Date());
		   	}
	  		return retreatTaskEntity;
       }
		//普通任务
		 if(activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior)
	        {
			 	parentExecution.setActivity(activityImpl);
				//创建新的代办任务
				TaskEntity retreatTaskEntity = TaskEntity.create(new Date());
				retreatTaskEntity.setProcessDefinitionId(taskEntity.getExecution().getProcessDefinitionId());
				retreatTaskEntity.setAssignee(assignee);
				retreatTaskEntity.setName(lastTask.getName());
				retreatTaskEntity.setExecutionId(parentExecution.getId());
				retreatTaskEntity.setTaskDefinitionKey(lastTask.getTaskDefinitionKey());
				retreatTaskEntity.setProcessInstanceId(lastTask.getProcessInstanceId());
				retreatTaskEntity.setFormKey(lastTask.getFormKey());
				retreatTaskEntity.getExecution().setActive(true);
		        retreatTaskEntity=TaskUtils.createAndInsert(parentExecution, retreatTaskEntity,assignee);
		        //记录创建的任务
		        Context.getCommandContext().getHistoryManager().recordActivityStart(retreatTaskEntity.getExecution());
		        Context.getCommandContext().getHistoryManager().recordTaskAssignment(retreatTaskEntity);
//		        Context.getCommandContext().getHistoryManager().recordTaskAssigneeChange(taskEntity.getId(), "1111111");
		        if (Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
		            Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
		              ActivitiEventBuilder.createEntityEvent(ActivitiEventType.TASK_CREATED, retreatTaskEntity));
		          }
		        retreatTaskEntity.fireEvent(TaskListener.EVENTNAME_CREATE);
		     	//更新现在的任务状态为退回
		        taskEntity.getExecution().setDeleteReason("retreat");
				commandContext.getTaskEntityManager().deleteTask(taskEntity, "retreat", false);
				currentExecution.remove();
				//查找当前任务关联的活动id是否完成。
				Map<String, Object> parameterMap = new HashMap<String, Object>();
		    	parameterMap.put("sql", "select * from ACT_HI_ACTINST where task_id_='" + taskEntity.getId() + "'");
		    	List<HistoricActivityInstance> historicActivityInstance = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
		    	if(historicActivityInstance!=null && historicActivityInstance.size() > 0){
					HistoricActivityInstanceEntity activityinstance = (HistoricActivityInstanceEntity) historicActivityInstance.get(0);
					activityinstance.setDeleteReason("retreat");
					activityinstance.setEndTime(new Date());
				   	}
		  		return retreatTaskEntity;
	        }
		 //并行模式
		 if(activityImpl.getActivityBehavior() instanceof BpmParallelMultiInstanceBehavior){
//			ExecutionEntity scopeparentExecution=new ExecutionEntity();
//			scopeparentExecution.setActive(false);
//			scopeparentExecution.setConcurrent(false);
//			scopeparentExecution.setScope(true);
//			scopeparentExecution.setSuspensionState(1);
//			String parentId= parentEntity.getId() +Math.round(Math.random()*1000000);
//			scopeparentExecution.setId(parentId);
//			scopeparentExecution.setParentId(parentEntity.getId());
//			scopeparentExecution.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
//			scopeparentExecution.setProcessInstance(parentEntity);
//			scopeparentExecution.setActivity(activityImpl);
//			Context.getCommandContext().getExecutionEntityManager().insert(scopeparentExecution);
			 throw new BpmException("该审批模式不支持退回");
		 }
		 return null;
		
	}
	
	
	 private ActivityImpl getActivity(
	            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
	        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
	                historicActivityInstanceEntity.getProcessDefinitionId())
	                .execute(Context.getCommandContext());
	        return processDefinitionEntity
	                .findActivity(historicActivityInstanceEntity.getActivityId());
	  }

	 //通过任务的优先级判断业务中退回的指向
	 private boolean findTransition(List<PvmTransition> currentTransition,HistoricActivityInstance activityIns,TaskEntity taskEntity) {
		 boolean flag = false;
		 for(PvmTransition transition:currentTransition){
				ActivityImpl dest = (ActivityImpl) transition.getSource();
	            ActivityBehavior activityBehavior = dest.getActivityBehavior();
				//针对串行的审批模式
				if(activityIns.getActivityId().equals(transition.getSource().getId())) {
					HistoricTaskInstanceEntity  currentTask =  Context.getCommandContext().getHistoricTaskInstanceEntityManager().findHistoricTaskInstanceById(activityIns.getTaskId());
					if(taskEntity.getPriority()>=currentTask.getPriority()){
						flag=true;
						break;
					}
				}
				if(activityBehavior instanceof GatewayActivityBehavior && !flag){
					flag = findTransition(dest.getIncomingTransitions(),activityIns,taskEntity);
				}
		 }
		 return flag;
	 }
}
