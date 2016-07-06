package com.yonyou.bpm.engine.cmd;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.BpmException;
/**
 * 查找活动后面的人工活动
 * @author zhaohb
 *
 */
public class FindNextActivitiesCmd implements Command<List<PvmActivity>> {
    private static Logger logger = LoggerFactory
            .getLogger(FindNextActivitiesCmd.class);
    private String processDefinitionId;
    private String activityId;
    private String taskId;
    private boolean ignoreConditions=false;
    
    //效率优化
    private ActivityExecution activityExecution;
    private Task  task;
    private ProcessDefinitionEntity processDefinitionEntity;
    /**
     * 获取任务的下一步人工活动(解析条件)
     * @param taskId
     */
    public FindNextActivitiesCmd(String taskId){
    	this.taskId=taskId;
    }
    /**
     * 获取任务的下一步人工活动(解析条件)
     * @param taskId
     */
    public FindNextActivitiesCmd(String taskId,boolean ignoreConditions){
    	this.taskId=taskId;
    	this.ignoreConditions=ignoreConditions;
    }
    /**
     * 效率提升
     * @param task
     * @param processDefinitionEntity
     * @param ignoreConditions
     */
    public FindNextActivitiesCmd(ActivityExecution activityExecution,Task  task,ProcessDefinitionEntity processDefinitionEntity,String activityId,boolean ignoreConditions){
    	this.activityExecution=activityExecution;
    	this.task=task;
    	this.processDefinitionEntity=processDefinitionEntity;
    	this.activityId=activityId;
    	this.ignoreConditions=ignoreConditions;
    }
    /**
     * 获取下一步活动
     * @param processDefinitionId
     * @param activityId
     */
    public FindNextActivitiesCmd(String processDefinitionId, String activityId) {
        this.processDefinitionId = processDefinitionId;
        this.activityId = activityId;
    }

    public List<PvmActivity> execute(CommandContext commandContext) {
    	if(task==null){
	    	if(taskId!=null&&!"".equals(taskId)){
	    		task =Context.getProcessEngineConfiguration().getTaskService().createTaskQuery().taskId(taskId).singleResult();
	    	}
    	}
    	if(task!=null){
    		processDefinitionId=task.getProcessDefinitionId();
    		activityId=task.getTaskDefinitionKey();
    		if(activityExecution==null){
    			activityExecution=(ExecutionEntity)Context.getProcessEngineConfiguration().getRuntimeService().createExecutionQuery().executionId(task.getExecutionId()).singleResult();
    		}
    	}
    	if(processDefinitionEntity==null){
    		processDefinitionEntity = Context
    				.getProcessEngineConfiguration().getDeploymentManager()
    				.findDeployedProcessDefinitionById(processDefinitionId);
    	}

        if (processDefinitionEntity == null) {
            throw new IllegalArgumentException(
                    "cannot find processDefinition : " + processDefinitionId);
        }
        ActivityImpl activity = processDefinitionEntity
                .findActivity(activityId);
        if(activity==null){
        	 throw new IllegalArgumentException(
                     "cannot find activity : " + activityId+"in processDefinition:"+processDefinitionId);
        }
        List<PvmActivity> pvmActivities = new ArrayList<PvmActivity>();
        return this.getNextActivities(pvmActivities,activity);
    }

    public List<PvmActivity> getNextActivities(List<PvmActivity> pvmActivities,PvmActivity pvmActivity) {
       if(pvmActivities==null){
    	   pvmActivities= new ArrayList<PvmActivity>();
       }
        List<PvmTransition> transitionsToTake=null;
        if(this.activityExecution!=null&&!ignoreConditions){
        	transitionsToTake=getTakeAbles(this.activityExecution, pvmActivity);
        }
        List<PvmTransition>  outgoingTransitions=pvmActivity.getOutgoingTransitions();
        if(outgoingTransitions==null||outgoingTransitions.size()==0)return pvmActivities;
        for (PvmTransition pvmTransition : outgoingTransitions) {
            PvmActivity targetActivity = pvmTransition.getDestination();
            
            if(targetActivity==null)continue;
            //并行网关不解析条件
            //需要过滤
            if(transitionsToTake!=null
            		&& !BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL.equals(pvmTransition.getSource().getProperty("type"))){
            	boolean contains=false;
            	for (PvmTransition pvmTransition2 : transitionsToTake) {
					if(pvmTransition2.getId().equals(pvmTransition.getId())){
						contains=true;
						break;
					}
				}
            	if(!contains){
            		continue;
            	}
            }
            if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(targetActivity.getProperty("type"))) {
            	boolean contain=false;
            	for (PvmActivity pvmActivity2 : pvmActivities) {
					if(pvmActivity2.getId().equals(targetActivity.getId())){
						contain=true;
					}
				}
            	if(!contain){
            		pvmActivities.add(targetActivity);
            	}
            } else {
            	List<PvmActivity>  result=this.getNextActivities(pvmActivities,targetActivity);
            	if(result!=null&&result.size()>0){
            		for (PvmActivity pvmActivity2 : result) {
            			boolean contain=false;
						for (PvmActivity pvmActivity21 : pvmActivities) {
							if(pvmActivity21.getId().equals(pvmActivity2.getId())){
								contain=true;
								break;
							}
						}
						if(!contain){
							pvmActivities.add(pvmActivity2);
						}
					}
            	}
            }
        }
        
        logger.info("查找到的下一步活动数量："+pvmActivities.size());
        return pvmActivities;
    }

	private List<PvmTransition> getTakeAbles(ActivityExecution execution,PvmActivity curPvmActivity) {
		if(curPvmActivity==null){
			throw new BpmException("活动不能为空！");
		}
		String defaultSequenceFlow = (String)curPvmActivity
				.getProperty("default");
		List<PvmTransition> transitionsToTake = new ArrayList<PvmTransition>();

		List<PvmTransition> outgoingTransitions = curPvmActivity
				.getOutgoingTransitions();
		for (PvmTransition outgoingTransition : outgoingTransitions) {
			if (defaultSequenceFlow == null
					|| !outgoingTransition.getId().equals(defaultSequenceFlow)) {
				Condition condition = (Condition) outgoingTransition
						.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
				// modify 路由条件判断》取得流程相关信息
				CommandContext commandContext = Context.getCommandContext();
				if (commandContext != null) {
					commandContext.addAttribute("curTransition",
							outgoingTransition);
					commandContext.addAttribute("curExecution", execution);
				}
				if (condition == null || condition.evaluate(execution)) {
					transitionsToTake.add(outgoingTransition);
				}
			}
		}
		if (transitionsToTake.size() == 0) {
			if (defaultSequenceFlow != null) {
				PvmTransition defaultTransition = execution.getActivity()
						.findOutgoingTransition(defaultSequenceFlow);
				if (defaultTransition != null) {
					transitionsToTake.add(defaultTransition);
				} else {
					throw new ActivitiException("Default sequence flow '"
							+ defaultSequenceFlow + "' could not be not found");
				}
			}
		}
		return transitionsToTake;
	}
}
