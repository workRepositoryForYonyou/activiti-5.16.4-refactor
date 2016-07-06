package com.yonyou.bpm.engine.service;

import java.util.Map;

import org.activiti.engine.impl.FormServiceImpl;
import org.activiti.engine.runtime.ProcessInstance;

import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmFormService")
public class BpmFormServiceImpl extends FormServiceImpl {

	@TracePoint
	@Override
	public void submitTaskFormData(
			@TraceValue(TraceKeys.TASK_ID) String taskId,
			Map<String, String> properties) {
		super.submitTaskFormData(taskId, properties);
	}

	@TracePoint
	@Override
	public ProcessInstance submitStartFormData(
			@TraceValue(TraceKeys.PROCESS_DEFINITION_ID) String processDefinitionId,
			@TraceValue(TraceKeys.BUSINESS_KEY) String businessKey,
			Map<String, String> properties) {
		return super.submitStartFormData(processDefinitionId, businessKey,
				properties);
	}

}
