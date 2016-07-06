package com.yonyou.bpm.engine.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.utils.TaskUtils;

/**
 * 创建协办任务,可以创建多个
 * @author zhangxya
 *
 */
public class AssistTaskCmd implements Command<Task>, Serializable {
	
	private static final long serialVersionUID = 1L;
	public String parentTaskId;
	public List<String> assistants;

	public AssistTaskCmd(String parentTaskId,  List<String> assistants) {
		this.parentTaskId = parentTaskId;
		this.assistants = assistants;
	}
	

	@Override
	public Task execute(CommandContext commandContext) {
		TaskEntity taskEntity=(TaskEntity)commandContext.getProcessEngineConfiguration().getTaskService().createTaskQuery().taskId(parentTaskId).singleResult();
		if(taskEntity==null){
			throw new BpmException("找不到任务："+this.parentTaskId);
		}
		if(assistants==null||assistants.size()==0){
			throw new  IllegalArgumentException("'assistants'不能为空！");
		}
		taskEntity.setVariableLocal("assistingConfirm",Boolean.FALSE);
		//已创建协办
		ExecutionEntity  parentExecution=taskEntity.getExecution();
		if(parentExecution==null){
			throw new ActivitiException("任务对应的执行实例不能为空！");
		}
		ExecutionEntity  execution=parentExecution.createExecution();
		execution.setVariableLocal("assist", Boolean.TRUE);
		TaskEntity resultTaskEntity=null;
		if(assistants.size()==1){
			resultTaskEntity=TaskUtils.createAndInsert(execution, taskEntity, assistants.get(0));
		}else{
			List<String> candidateUsers=new ArrayList<String>(assistants.size());
			resultTaskEntity=TaskUtils.createAndInsertWithCandidateUsers(execution, taskEntity, candidateUsers);
		}
		resultTaskEntity.setVariableLocal("createType", "assist");
		return resultTaskEntity;
	}

}
