package com.yonyou.bpm.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.helper.ClassDelegate;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.yonyou.bpm.delegate.BpmTaskListener;
import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;

/**
 * 超时提醒执行器
 * 
 * @author zhangxya
 *
 */
public class TaskTimeoutJob {
	private static Logger logger = LoggerFactory
			.getLogger(TaskTimeoutJob.class);
	public static final int TYPE_ARRIVAL = 0;
	public static final int TYPE_COMPLETE = 1;
	public static final int TYPE_TIMEOUT = 2;

	@Autowired
	private BpmEngineConfiguration processEngineConfiguration;

	@Autowired
	private TaskService taskService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Scheduled(cron = "0 0/30 * * * ?")
	public void execute() throws Exception {
		logger.info("开始查询所有运行时的任务");
		List<Task> tasks = taskService.createTaskQuery()
				.taskDueBefore(new Date()).list();
		logger.info("查询到的任务条数" + tasks.size());
		for (Task task : tasks) {
			if (task.getDueDate() != null) {
				// logger.info("任务Id:" + task.getId() + "设置的超时时间为" +
				// task.getDueDate());
				// Calendar calendar = Calendar.getInstance();
				// calendar.setTime(task.getDueDate());
				// Date noticeDate = calendar.getTime();
				// Date now = new Date();
				// 现在的时间已经超过了期限的时间
				// if ((now.getTime() > noticeDate.getTime())){
				// 针对任务调用超时提醒逻辑
				if (task instanceof TaskEntity)
					this.fireEvent(BpmTaskListener.EVENTNAME_OUTTIME,
							task.getTaskDefinitionKey(),
							task.getProcessDefinitionId(), (TaskEntity) task);
				// }
			}
		}
		logger.info("end");
	}

	public void fireEvent(String taskEventName, String taskDefinitionKey,
			String processInstanceId, TaskEntity task) {

		TaskDefinition taskDefinition = this.getTaskDefinition(
				taskDefinitionKey, processInstanceId);
		task.setTaskDefinition(taskDefinition);
		if (taskDefinition != null) {
			List<TaskListener> taskEventListeners = taskDefinition
					.getTaskListener(taskEventName);
			if (taskEventListeners != null) {
				for (TaskListener taskListener : taskEventListeners) {
					String className = ((ClassDelegate) taskListener)
							.getClassName();
					try {
						// 得到对象
						Class c = Class.forName(className);
						Object taskListenerObj = c.newInstance();
						((TaskListener) taskListenerObj).notify(null);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public TaskDefinition getTaskDefinition(String taskDefinitionKey,
			String processDefinitionId) {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);

		TaskDefinition taskDefinition = processDefinition.getTaskDefinitions()
				.get(taskDefinitionKey);
		return taskDefinition;
	}

}
