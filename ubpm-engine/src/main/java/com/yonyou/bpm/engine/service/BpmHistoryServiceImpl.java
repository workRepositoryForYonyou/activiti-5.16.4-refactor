package com.yonyou.bpm.engine.service;

import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

import com.yonyou.bpm.engine.impl.BpmHistoricActivityInstanceQueryImpl;
import com.yonyou.bpm.engine.impl.BpmHistoricProcessInstanceQueryImpl;
import com.yonyou.bpm.engine.impl.BpmHistoricTaskInstanceQueryImpl;
import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmHistoryService")
public class BpmHistoryServiceImpl extends HistoryServiceImpl {
	
	public BpmHistoryServiceImpl(ProcessEngineConfigurationImpl configuration) {
		super(configuration);
	}

	@TracePoint
	@Override
	public void deleteHistoricProcessInstance(
			@TraceValue(TraceKeys.PROCESS_INSTANCE_ID) String processInstanceId) {
		super.deleteHistoricProcessInstance(processInstanceId);
	}

	@TracePoint
	@Override
	public void deleteHistoricTaskInstance(
			@TraceValue(TraceKeys.TASK_ID) String taskId) {
		super.deleteHistoricTaskInstance(taskId);
	}
	
	@Override
	public HistoricProcessInstanceQuery createHistoricProcessInstanceQuery() {
		return new BpmHistoricProcessInstanceQueryImpl(commandExecutor);
	}
	public HistoricActivityInstanceQuery createHistoricActivityInstanceQuery() {
	    return new BpmHistoricActivityInstanceQueryImpl(commandExecutor);
	  }
	@Override
	 public HistoricTaskInstanceQuery createHistoricTaskInstanceQuery() {
		return new BpmHistoricTaskInstanceQueryImpl(commandExecutor, processEngineConfiguration.getDatabaseType());
	}

	

}
