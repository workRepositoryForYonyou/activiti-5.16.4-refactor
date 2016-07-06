package com.yonyou.bpm.engine.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.utils.TaskUtils;
/**
 * 加签命令
 * @author zhaohb
 *
 */
public class CounterSignCmd extends NeedsActiveTaskCmd<Object> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(CounterSignCmd.class);
    //加出来的
    public static final String COUNTERSIGN_COLLECTION="counterSignCollection";
    //正在执行的
    public static final String COUNTERSIGN_ING="counterSigning";
    public static final String OPERATOR_ADD="add";
    public static final String OPERATOR_REMOVE="remove";
    private String operateType;
    private String activityId;
    private String assignee;
    private List<String> assignees;
    private String processInstanceId;
    private CommandContext commandContext;
    private TaskEntity task;

    public CounterSignCmd(String operateType, String assignee, String taskId) {
    	super(taskId);
        this.operateType = operateType;
        this.assignee = assignee;
        this.taskId = taskId;
    }
    public CounterSignCmd(String operateType, List<String> assignees, String taskId) {
    	super(taskId);
        this.operateType = operateType;
        this.assignees = assignees;
        this.taskId = taskId;
    }
    protected Object execute(CommandContext commandContext, TaskEntity task) {
    	this.task=task;
        this.commandContext = commandContext;
        ExecutionEntity processIntacnceExutionEntity=commandContext.getExecutionEntityManager().findExecutionById(task.getProcessInstanceId());
    	if(processIntacnceExutionEntity!=null&&processIntacnceExutionEntity.isSuspended()){
    		throw new ActivitiException("流程实例已挂起！");
    	}
        Boolean counterSigning=(Boolean)task.getVariableLocal("counterSigning");
        if(counterSigning!=null&&counterSigning.booleanValue()){
        	throw new BpmException("任务："+this.taskId+"已经是加签中！");
        }
        activityId = task.getExecution().getActivityId();
        processInstanceId = task.getProcessInstanceId();
        logger.info("任务："+this.taskId+"正在进行加签动作："+this.operateType);
        if (operateType.equalsIgnoreCase("add")) {
            addInstance();
        } else if (operateType.equalsIgnoreCase("remove")) {
            removeInstance();
        }

        return null;
    
    }


    /**
     * <li>加签
     */
    public void addInstance() {
    	String multiInstance=(String)getActivity().getProperty("multiInstance");
    	if(multiInstance==null||"".equals(multiInstance.trim())){
    		throw new BpmException("加签只能针对多实例的人工任务！");
    	}
        if (multiInstance.equals("parallel")) {
            addParallelInstance();
        } else {
            addSequentialInstance();
        }
    }

    /**
     * <li>减签
     */
    public void removeInstance() {
    }

    /**
     * <li>添加一条并行实例
     */
    private void addParallelInstance() {
    	Collection<String> col=getCollection();
    	//前加签
    	task.setVariableLocal("counterSigning", Boolean.TRUE);
    	
    	ExecutionEntity pExecution=task.getExecution();
    	
    	ExecutionEntity execution=pExecution.createExecution();
    	
    	pExecution.setActive(false);
    	
    	execution.setVariableLocal("counterSignCollection", col);
    	execution.setVariableLocal("nrOfCounterSignCompleted", 0);
    	//遍历
		for (String assigneeTemp : col) {
			TaskEntity  taskEntityTemp =TaskUtils.createAndInsert(execution, task, assigneeTemp);
			taskEntityTemp.setVariableLocal("createType", "countSignParrallel");
		}
    	
    	
    }
   
    /**
     * <li>给串行实例集合中添加一个审批人
     */
    private void addSequentialInstance() {
    	Collection<String> col=getCollection();
    	//前加签
    	task.setVariableLocal("counterSigning", Boolean.TRUE);
    	
    	ExecutionEntity pExecution=task.getExecution();
    	
    	ExecutionEntity execution=pExecution.createExecution();
    	
    	pExecution.setActive(false);
    	
    	execution.setVariableLocal("counterSignCollection", col);
    	execution.setVariableLocal("nrOfCounterSignCompleted", 0);
    	TaskEntity taskEntityTemp=TaskUtils.createAndInsert(execution, task, col.iterator().next());
    	taskEntityTemp.setVariableLocal("createType", "countSignSequence");
    }
   

    private Collection<String> getCollection(){
    	Collection<String> col=new ArrayList<String>();
    	if(assignee!=null&&!"".equals(assignee.trim())){
    		col.add(assignee);
    	}else if(assignees!=null&&assignees.size()>0){
    		col.addAll(assignees);
    	}
    	if(col.size()==0){
    		throw new BpmException("加签用户不能为空！");
    	}
    	
    	return col;
    }
    
   

    /**
     * <li>返回当前节点对象
     */
    protected ActivityImpl getActivity() {
        return this.getProcessDefinition().findActivity(activityId);
    }

    /**
     * <li>返回流程定义对象
     */
    protected ProcessDefinitionImpl getProcessDefinition() {
        return this.getProcessInstanceEntity().getProcessDefinition();
    }

    /**
     * <li>返回流程实例的根执行对象
     */
    protected ExecutionEntity getProcessInstanceEntity() {
        return commandContext.getExecutionEntityManager().findExecutionById(
                processInstanceId);
    }

    /**
     * <li>添加本地变量
     */
    protected void setLocalVariable(ActivityExecution execution,
            String variableName, Object value) {
        execution.setVariableLocal(variableName, value);
    }
    
}
