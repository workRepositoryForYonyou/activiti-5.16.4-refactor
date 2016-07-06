package com.yonyou.bpm.engine.service;

import org.activiti.engine.impl.ManagementServiceImpl;
import org.activiti.engine.runtime.JobQuery;

import com.yonyou.bpm.engine.impl.BpmJobQueryImpl;
import com.yonyou.bpm.trace.TraceKeys;
import com.yonyou.bpm.trace.TracePoint;
import com.yonyou.bpm.trace.TraceTarget;
import com.yonyou.bpm.trace.TraceValue;

@TraceTarget("BpmManagementService")
public class BpmManagementServiceImpl extends ManagementServiceImpl {

	@TracePoint
	@Override
	public void deleteJob(@TraceValue(TraceKeys.JOB_ID) String jobId) {
		super.deleteJob(jobId);
	}

	@TracePoint
	@Override
	public void executeJob(@TraceValue(TraceKeys.JOB_ID) String jobId) {
		super.executeJob(jobId);
	}

	@TracePoint
	@Override
	public void setJobRetries(@TraceValue(TraceKeys.JOB_ID) String jobId,
			int retries) {
		super.setJobRetries(jobId, retries);
	}
	@Override
	public JobQuery createJobQuery() {
		 return new BpmJobQueryImpl(commandExecutor);
	}
}
