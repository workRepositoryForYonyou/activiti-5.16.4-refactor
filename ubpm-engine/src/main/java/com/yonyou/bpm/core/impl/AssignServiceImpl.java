package com.yonyou.bpm.core.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignService;
/**
 * 指派服务接口
 * @author zhaohb
 *
 */
public class AssignServiceImpl implements AssignService{
	private static Logger logger = LoggerFactory
			.getLogger(AssignServiceImpl.class);
	ProcessEngineConfiguration processEngineConfiguration;
	
	public AssignServiceImpl (){}
	
	public AssignServiceImpl (ProcessEngineConfiguration processEngineConfiguration){
		this.processEngineConfiguration=processEngineConfiguration;
		
	}
	@Override
	public void save(AssignInfo assignInfo) {
		String taskId=assignInfo.getTaskId();
		String executionId=assignInfo.getExecutionId();
		if(taskId!=null&&!"".equals(taskId.trim())){
			this.processEngineConfiguration.getTaskService().setVariable(taskId, AssignInfo.ASSIGNINFO_PARAM, assignInfo);
		}else if(executionId!=null&&!"".equals(executionId)){
			this.processEngineConfiguration.getRuntimeService().setVariable(executionId,  AssignInfo.ASSIGNINFO_PARAM, assignInfo);
		}else{
			throw new IllegalArgumentException("'taskId' and 'executionId' can not both be null!");
		}
	}

	@Override
	public AssignInfo findAssignInfo(String taskId) {
		Object obj=null;
		try{
			obj=this.processEngineConfiguration.getTaskService().getVariable(taskId, AssignInfo.ASSIGNINFO_PARAM);
		}catch(ActivitiObjectNotFoundException e){
			logger.error(e.getMessage());
		}
		if(obj==null){
			List<HistoricVariableInstance> historicVariableInstances=this.processEngineConfiguration.getHistoryService()
			.createHistoricVariableInstanceQuery().taskId(taskId).variableName(AssignInfo.ASSIGNINFO_PARAM).list();
			if(historicVariableInstances==null||historicVariableInstances.size()==0)return null;
			Collections.sort(historicVariableInstances, new Comparator<HistoricVariableInstance>(){

				@Override
				public int compare(HistoricVariableInstance o1,
						HistoricVariableInstance o2) {
					if(o1.getCreateTime().before(o2.getCreateTime()))return -1;
					if(o1.getCreateTime().after(o2.getCreateTime()))return 1;
					return 0;
				}
				
			});
			obj=historicVariableInstances.get(0).getValue();
		}
		if(obj==null)return null;
		
		return (AssignInfo)obj;
	}
	@Override
	public AssignInfo findAssignInfoExcludeTask(String executionId){
		Object obj=null;
		try{
			obj=this.processEngineConfiguration.getRuntimeService().getVariable(executionId, AssignInfo.ASSIGNINFO_PARAM);
		}catch(ActivitiObjectNotFoundException e){
			logger.error(e.getMessage());
		}
		if(obj==null){
			List<HistoricActivityInstance>  list=this.processEngineConfiguration.getHistoryService().createHistoricActivityInstanceQuery().executionId(executionId).list();
			String processInstanceId=null;
			if(list!=null&&list.size()>0){
				processInstanceId=list.get(0).getProcessInstanceId();
			}
			List<HistoricVariableInstance> historicVariableInstances=this.processEngineConfiguration.getHistoryService()
			.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).excludeTaskVariables().variableName(AssignInfo.ASSIGNINFO_PARAM).list();
			if(historicVariableInstances==null||historicVariableInstances.size()==0)return null;
			Collections.sort(historicVariableInstances, new Comparator<HistoricVariableInstance>(){

				@Override
				public int compare(HistoricVariableInstance o1,
						HistoricVariableInstance o2) {
					if(o1.getCreateTime().before(o2.getCreateTime()))return -1;
					if(o1.getCreateTime().after(o2.getCreateTime()))return 1;
					return 0;
				}
				
			});
			obj=historicVariableInstances.get(0).getValue();
		}
		if(obj==null)return null;
		
		return (AssignInfo)obj;
	}
	@Override
	public void deleteRunningAssignInfo(String executionId){
		this.processEngineConfiguration.getRuntimeService().removeVariable(executionId, AssignInfo.ASSIGNINFO_PARAM+executionId);
	}
	@Override
	public void deleteAssignInfo(String taskId){
		this.processEngineConfiguration.getTaskService().removeVariable(taskId, AssignInfo.ASSIGNINFO_PARAM+taskId);
	}
	
	public ProcessEngineConfiguration getProcessEngineConfiguration() {
		return processEngineConfiguration;
	}

	public void setProcessEngineConfiguration(
			ProcessEngineConfiguration processEngineConfiguration) {
		this.processEngineConfiguration = processEngineConfiguration;
	}

}
