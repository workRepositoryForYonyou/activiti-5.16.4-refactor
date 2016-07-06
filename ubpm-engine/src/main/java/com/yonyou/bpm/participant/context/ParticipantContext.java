package com.yonyou.bpm.participant.context;

import java.lang.reflect.Field;
import java.util.Map;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.participant.ParticipantFactory;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.config.ParticipantConfig;
import com.yonyou.bpm.participant.config.ParticipantFilterConfig;

/**
 * 参与者上下文
 * 
 * @author zhaohb
 *
 */
public class ParticipantContext {
	private static Logger logger = LoggerFactory
			.getLogger(ParticipantContext.class);
	/**
	 * 参与者配置信息
	 */
	private Map<String, ParticipantConfig> participantConfigs;
	private Map<String, ParticipantFilterConfig> participantFilterConfigs;
	/** 流程定义中配置的参与者 */
	private Map<String, ProcessParticipantItem> allProcessParticipantItems;
	/** 其他 */
	private Map<String, Object> others;

	/**
	 * 获取命令
	 * 
	 * @return
	 */
	public static Command<?> getCommand() {
		return Context.getCommandContext().getCommand();
	}

	/**
	 * 获取流程定义
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ProcessDefinitionImpl getProcessDefinition() {
		Execution execution = getExecution();
		if (execution != null) {
			return ((ExecutionEntity) execution).getProcessDefinition();
		}
		Command command = getCommand();
		try {
			Field field = command.getClass().getDeclaredField(
					"processDefinitionId");
			field.setAccessible(true);
			Object processDefinitionIdObj = field.get(command);
			if (processDefinitionIdObj == null)
				return null;
			String processDefinitionId = (String) processDefinitionIdObj;
			return Context.getProcessEngineConfiguration()
					.getDeploymentManager()
					.findDeployedProcessDefinitionById(processDefinitionId);
		} catch (Exception e) {
			// 异常不做处理
			logger.debug("command中没有流程定义ID");
		}
		return null;
	}

	/**
	 * 获取流程实例
	 * 
	 * @return
	 */
	public ProcessInstance getProcessInstance() {
		Execution execution = getExecution();
		if (execution == null) {
			return null;
		}
		return ((ExecutionEntity) execution).getProcessInstance();
	}

	/**
	 * 获取执行实例
	 * 
	 * @return
	 */
	public Execution getExecution() {

		if (Context.isExecutionContextActive()) {
			return Context.getExecutionContext().getExecution();
		}
		TaskEntity taskEntity = (TaskEntity) getTask();
		if (taskEntity != null) {
			return taskEntity.getExecution();
		}
		return null;
	}

	/**
	 * 获取任务
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Task getTask() {
		if (getCommand() == null)
			return null;
		String taskId = null;
		if (getCommand() instanceof NeedsActiveTaskCmd) {
			NeedsActiveTaskCmd needsActiveTaskCmd = (NeedsActiveTaskCmd) getCommand();
			taskId = needsActiveTaskCmd.getTaskId();

		}
		if (taskId == null || "".equals(taskId.trim())) {
			Command command = getCommand();
			try {
				Field field = command.getClass().getDeclaredField("taskId");
				field.setAccessible(true);
				Object taskIdObj = field.get(command);
				if (taskIdObj == null)
					return null;
				taskId = (String) taskIdObj;
			} catch (Exception e) {
				// 异常不做处理
				logger.debug("command中没有任务ID");
			}
		}
		if(taskId==null||"".equals(taskId.trim()))return null;
		return Context.getProcessEngineConfiguration().getTaskService()
				.createTaskQuery().taskId(taskId).singleResult();

	}

	public Map<String, ParticipantConfig> getParticipantConfigs() {
		if (participantConfigs == null || participantConfigs.size() == 0) {
			participantConfigs = ParticipantFactory.getInstance()
					.getParticipantConfigs();
		}
		return participantConfigs;
	}

	public void setParticipantConfigs(
			Map<String, ParticipantConfig> participantConfigs) {
		this.participantConfigs = participantConfigs;
	}

	public Map<String, ParticipantFilterConfig> getParticipantFilterConfigs() {
		if (participantFilterConfigs == null
				|| participantFilterConfigs.size() == 0) {
			participantFilterConfigs = ParticipantFactory.getInstance()
					.getParticipantFilterConfigs();
		}
		return participantFilterConfigs;
	}

	public void setParticipantFilterConfigs(
			Map<String, ParticipantFilterConfig> participantFilterConfigs) {
		this.participantFilterConfigs = participantFilterConfigs;
	}

	public Map<String, ProcessParticipantItem> getAllProcessParticipantItems() {
		return allProcessParticipantItems;
	}

	public void setAllProcessParticipantItems(
			Map<String, ProcessParticipantItem> allProcessParticipantItems) {
		this.allProcessParticipantItems = allProcessParticipantItems;
	}

	public Map<String, Object> getOthers() {
		return others;
	}

	public void setOthers(Map<String, Object> others) {
		this.others = others;
	}
}
