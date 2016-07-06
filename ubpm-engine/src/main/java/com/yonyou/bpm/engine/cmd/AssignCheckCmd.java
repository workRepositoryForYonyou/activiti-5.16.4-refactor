package com.yonyou.bpm.engine.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.gov.customs.casp.sdk.h4a.entity.ObjectsDetail;
import cn.gov.customs.casp.sdk.h4a.enumdefines.ViewCategory;
import cn.gov.customs.casp.sdk.h4a.ogu.ws.ObjectCategory;
import cn.gov.customs.casp.sdk.h4a.ogu.ws.OrganizationCategory;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignCheckResult;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignInfoItem;
import com.yonyou.bpm.core.category.CategoryService;
import com.yonyou.bpm.core.entity.CategoryEntity;
import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;
import com.yonyou.bpm.engine.service.BpmRepositoryServiceImpl;
import com.yonyou.bpm.helper.HGOguBeanReaderHelper;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ProcessParticipantDetail;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.utils.AssignInfoUtils;
import com.yonyou.bpm.utils.MultiInstanceUtils;
import com.yonyou.bpm.utils.ParticipantUtils;
/**
 * 指派前检查
 * 返回为空为无下一步
 * @author zhaohb
 *
 */
public class AssignCheckCmd extends NeedsActiveTaskCmd<AssignCheckResult> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory
            .getLogger(AssignCheckCmd.class);
    //流程定义Key
    private String processDefinitionKey;
    //流程定义ID
    private String processDefinitionId;
    //租户ID
    private String tenantId;
    //当前活动ID
    private String activityId;
    private ActivityImpl activityImpl;
    //当前执行实例
    private String executionId;
    //效率优化
    ExecutionEntity execution;
    //视角
    private String viewCode;
    
    private Task task;
    private ProcessDefinitionEntity processDefinitionEntity;
    /**
     * 根据当前任务创建
     * @param taskId
     */
    public AssignCheckCmd(String taskId){
    	super(taskId);
    }
    /**
     * 根据流程定义key、流程定义Id,租户Id,当前活动id、执行实例Id创建
     * @param processDefinitionKey
     * @param processDefinitionId
     * @param tenantId
     * @param activityId
     * @param executionId
     */
    public AssignCheckCmd(String processDefinitionKey,String processDefinitionId,String tenantId, String activityId,String executionId) {
    	super(null);
    	this.processDefinitionKey=processDefinitionKey;
        this.processDefinitionId = processDefinitionId;
        this.tenantId=tenantId;
        this.activityId = activityId;
        this.executionId=executionId;
    }
    /**
     * 根据流程定义ID、当前活动ID、执行实例ID创建
     * @param processDefinitionId
     * @param activityId
     * @param executionId
     */
    public AssignCheckCmd(String processDefinitionId, String activityId,String executionId) {
    	super(null);
        this.processDefinitionId = processDefinitionId;
        this.activityId = activityId;
        this.executionId=executionId;
    }
	@Override
	public AssignCheckResult execute(CommandContext commandContext) {
		long start=System.nanoTime();
		//初始化参数
		initParam(commandContext);
		//获取指派活动
		List<PvmActivity> assignAbleList = getAssinAbleList(commandContext);
		
		AssignCheckResult assignCheckResult = new AssignCheckResult();
		boolean isLeaving=true;
		//本步多实例的情况
		String multiInstance=(String)activityImpl.getProperty("multiInstance");
		if(multiInstance!=null&&!"".equals(multiInstance.trim())){
			isLeaving=isLeaving();
		}
		
		if(!isLeaving){
			assignCheckResult.setAssignAble(false);
			assignCheckResult.setDescription("本活动未完成!");
			return assignCheckResult;
		
		}
		logger.info("指派检查：获取可指派活动耗时间："+(System.nanoTime()-start));
		//无下一步，无可指派的环节
		if (assignAbleList==null) {
			assignCheckResult.setAssignAble(false);
			assignCheckResult.setDescription("没有下一步!");
			return assignCheckResult;
		}
		if (assignAbleList.size() == 0) {
			assignCheckResult.setAssignAble(false);
			assignCheckResult.setDescription("有下一步，但是没有可指派的环节！");
			return assignCheckResult;
		}
		assignCheckResult.setAssignAble(true);
		AssignInfo assignInfo = new AssignInfo();
		List<AssignInfoItem> assignInfoItems = new ArrayList<AssignInfoItem>();
		for (PvmActivity pvmActivity : assignAbleList) {
		
			AssignInfoItem assignInfoItem = new AssignInfoItem();
			assignInfoItem.setActivityId(pvmActivity.getId());
			assignInfoItem.setActivityName((String) pvmActivity
					.getProperty("name"));
			//绑定参与者信息
			assignInfoItem=bindParticipants(commandContext, pvmActivity, assignInfoItem);
			assignInfoItems.add(assignInfoItem);
			
			//查询被指派的活动历史处理用户
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			if(pvmActivity.getId().contains("ApproveUserTask")){
//				String sql = "select * from ACT_HI_ACTINST where PROC_DEF_ID_='"+task.getProcessDefinitionId()+"' and PROC_INST_ID_='"+task.getProcessInstanceId()+"' and ACT_ID_ = '"+pvmActivity.getId()+"' AND END_TIME_ is not null";
				String sql = "select * from ACT_HI_TASKINST where PROC_DEF_ID_='"+task.getProcessDefinitionId()+"' and PROC_INST_ID_='"+task.getProcessInstanceId()+"' and TASK_DEF_KEY_ = '"+pvmActivity.getId()+"' AND END_TIME_ is not null";
				parameterMap.put("sql", sql);
//				List<HistoricActivityInstance> historicApprovenstances = Context.getCommandContext().getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByNativeQuery(parameterMap, 0, 100);
				List<HistoricTaskInstance> historicApprovenstances = Context.getCommandContext().getHistoricTaskInstanceEntityManager().findHistoricTaskInstancesByNativeQuery(parameterMap, 0, 100);
				Participant[] participants = new Participant[historicApprovenstances.size()];
				//该需求这样修改不合理，engine中引入三统一让引擎丧失扩展性
				HGOguBeanReaderHelper hbrh = new HGOguBeanReaderHelper();
				for (int i = 0; i < historicApprovenstances.size(); i++) {
					HistoricTaskInstance historicApprovenstance = historicApprovenstances.get(i);
					if(historicApprovenstance.getAssignee()!=null&&!"".equals(historicApprovenstance.getAssignee()) && viewCode!=null){
						try {
							ObjectsDetail[] objectDetail = hbrh.getBeanObjectsDetail(viewCode, ViewCategory.ViewCode, historicApprovenstance.getAssignee(), ObjectCategory.USER_GUID, "",  OrganizationCategory.NONE, "");
							if(objectDetail!=null && objectDetail.length>0) {
								String name = objectDetail[0].getDisplay_name();
								participants[i] = new Participant(historicApprovenstance.getAssignee(),"",name);
								assignInfoItem.setHisParticipants(participants);
							}
							participants[i] = new Participant(historicApprovenstance.getAssignee(),"","jjjj");
							assignInfoItem.setHisParticipants(participants);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
			}
		}
		assignInfo.setExecutionId(executionId);
		assignInfo.setAssignInfoItems(assignInfoItems
				.toArray(new AssignInfoItem[0]));
		assignCheckResult.setAssignInfo(assignInfo);
		logger.info("指派检查：总共耗时间："+(System.nanoTime()-start));
		return assignCheckResult;
	}
	private boolean  isLeaving(){
		boolean isLeavAble=false;
		ActivityBehavior activityBehavior=this.activityImpl.getActivityBehavior();
		MultiInstanceActivityBehavior multiInstanceActivityBehavior=null;
		if(activityBehavior instanceof MultiInstanceActivityBehavior){
			 multiInstanceActivityBehavior=(MultiInstanceActivityBehavior)activityBehavior;
		}
		if(multiInstanceActivityBehavior==null){
			return true;
		}
		String multiInstance=(String)activityImpl.getProperty("multiInstance");
		//并行
		if(MultiInstanceUtils.TYPE_PARALLEL.equals(multiInstance)){
		    int nrOfInstances =  MultiInstanceUtils.getLoopVariable(execution,  MultiInstanceUtils.NUMBER_OF_INSTANCES);
		    ExecutionEntity executionEntity = (ExecutionEntity) execution;
		    List<ActivityExecution> joinedExecutions = executionEntity.findInactiveConcurrentExecutions(execution.getActivity());
		    if (joinedExecutions.size()+1 >= nrOfInstances || MultiInstanceUtils.completionConditionSatisfiedBeforeComplete(multiInstanceActivityBehavior,execution)) {
		    	isLeavAble=true;
		    }
		//串行
		}else{
			int loopCounter =( MultiInstanceUtils.getLoopVariable(execution, multiInstanceActivityBehavior.getCollectionElementIndexVariable())) + 1;
		    int nrOfInstances = MultiInstanceUtils.getLoopVariable(execution, MultiInstanceUtils.NUMBER_OF_INSTANCES);
		    
		    if (loopCounter >= nrOfInstances ||  MultiInstanceUtils.completionConditionSatisfiedBeforeComplete(multiInstanceActivityBehavior,execution)) {
		    	isLeavAble=true;
		    }
		}
		return isLeavAble;
	}
	private List<PvmActivity> getAssinAbleList(CommandContext commandContext){
		BpmEngineConfiguration bpmEngineConfiguration = null;
		List<PvmActivity> list = null;
		bpmEngineConfiguration = (BpmEngineConfiguration) commandContext
				.getProcessEngineConfiguration();
		if(task!=null){
			list=bpmEngineConfiguration.getManagementService().executeCommand(new FindNextActivitiesCmd(execution,task, processDefinitionEntity, null,false));;
		}else{
			list = bpmEngineConfiguration.getManagementService().executeCommand(new FindNextActivitiesCmd(execution,null, processDefinitionEntity, activityId,false));;
		}
		if (list == null || list.size() == 0) {
			return null;
		}
		List<PvmActivity> assignAbleList = new ArrayList<PvmActivity>();
		// 获取可指派的活动
		for (PvmActivity pvmActivity : list) {
			if (AssignInfoUtils.isAssignAble(pvmActivity)) {
				assignAbleList.add(pvmActivity);
			}
		}
		return assignAbleList;
	}
	private void initParam(CommandContext commandContext) {
		if(task==null){
			if (taskId != null && !"".equals(taskId.trim())) {
				task = commandContext.getProcessEngineConfiguration()
						.getTaskService().createTaskQuery().taskId(taskId)
						.singleResult();
				if(task==null){
					throw new ActivitiException("找不到ID为'"+taskId+"'的任务！");
				}
			}
		}
		if(task!=null){
			this.processDefinitionId = task.getProcessDefinitionId();
			this.activityId = task.getTaskDefinitionKey();
			this.executionId = task.getExecutionId();
		}
		if(executionId!=null&&!"".equals(executionId)){
			execution=(ExecutionEntity)  commandContext.getProcessEngineConfiguration()
			.getRuntimeService().createExecutionQuery()
			.executionId(executionId).singleResult();
		}
		BpmRepositoryServiceImpl bpmRepositoryServiceImpl = (BpmRepositoryServiceImpl) commandContext
				.getProcessEngineConfiguration().getRepositoryService();
		if(processDefinitionEntity==null){
			processDefinitionEntity = bpmRepositoryServiceImpl
					.getProcessDefinition(processDefinitionKey,
							processDefinitionId, tenantId, true);
		}
		if (processDefinitionEntity == null) {
			throw new BpmException("找不到流程定义");
		}
		this.processDefinitionId = processDefinitionEntity.getId();
		if (activityId == null) {
			this.activityId = processDefinitionEntity.getInitial().getId();

		}
		activityImpl=processDefinitionEntity.findActivity(activityId);
		this.setApplicationView(commandContext);

	}
	private AssignInfoItem bindParticipants(CommandContext commandContext,PvmActivity pvmActivity,AssignInfoItem assignInfoItem){
		BpmEngineConfiguration bpmEngineConfiguration = (BpmEngineConfiguration) commandContext
				.getProcessEngineConfiguration();
		long start=System.nanoTime();
		
		List<String> list2 =commandContext.getProcessEngineConfiguration().getManagementService().executeCommand(new FindParticipantsCmd(processDefinitionEntity, pvmActivity.getId(),execution));
		long end1=System.nanoTime();
		logger.info("指派检查：查询用户ID消耗时间："+(end1-start));
		if (list2 == null || list2.size() == 0) {
			return assignInfoItem;
		}
		ProcessParticipantItem processParticipantItem = new ProcessParticipantItem();
		List<ProcessParticipantDetail> processParticipantDetailList = new ArrayList<ProcessParticipantDetail>();
		List<Participant> participantList=new ArrayList<Participant>();
		for (String string : list2) {
			Participant  participant =ParticipantUtils.getParticipant(string);
			if(participant!=null){
				participantList.add(participant);
			}else{
				ProcessParticipantDetail processParticipantDetail = new ProcessParticipantDetail();
				processParticipantDetail.setId(string);
				processParticipantDetailList.add(processParticipantDetail);
			}
		}
		processParticipantItem.setDetails(processParticipantDetailList
				.toArray(new ProcessParticipantDetail[0]));
		if(processParticipantItem.getDetails()!=null&&processParticipantItem.getDetails().length>0){
			List<Participant> participantListTemp = bpmEngineConfiguration
					.getParticipantService().getUserAdapter()
					.find(processParticipantItem, null);
			if(participantListTemp!=null&&participantListTemp.size()>0){
				participantList.addAll(participantListTemp);
			}
		}
		
		long userTime=System.nanoTime()-end1;
		
		logger.info("指派检查：将用户ID转换为用户消耗时间："+userTime);
		
		if (participantList == null || participantList.size() == 0) {
			throw new BpmException("参与者服务配置的用户查找借口出错了！");
		}
		assignInfoItem.setParticipants(participantList
				.toArray(new Participant[0]));
		return assignInfoItem;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public String getActivityId() {
		return activityId;
	}
	public ActivityImpl getActivityImpl() {
		return activityImpl;
	}
	public String getExecutionId() {
		return executionId;
	}
	public String getTaskId() {
		return taskId;
	}
	public ExecutionEntity getExecution() {
		return execution;
	}
	public Task getTask() {
		return task;
	}
	public ProcessDefinitionEntity getProcessDefinitionEntity() {
		return processDefinitionEntity;
	}
	//AssignCheckCmd可以只传任务，但是流程未发起时，也可能需要指派，建议后续将AssignCheckCmd翻开为针对task的和针对流程定义的
	@Override
	protected AssignCheckResult execute(CommandContext commandContext,
			TaskEntity task) {
		return null;
	}
	public String getViewCode() {
		return viewCode;
	}
	public void setViewCode(String viewCode) {
		this.viewCode = viewCode;
	}
	
	private void setApplicationView(CommandContext commandContext){
		BpmEngineConfiguration bpmEngineConfiguration = null;
		bpmEngineConfiguration = (BpmEngineConfiguration) commandContext.getProcessEngineConfiguration();
		DeploymentQuery query = bpmEngineConfiguration.getRepositoryService().createDeploymentQuery();
		CategoryService cateService= bpmEngineConfiguration.getCategoryService();
		query.deploymentId(processDefinitionEntity.getDeploymentId());
		List<Deployment> list = query.list();
		if(list!=null&&list.size()>0){
			CategoryEntity category = (CategoryEntity) cateService.getCategoryById(list.get(0).getCategory());
			if(category!=null){
				this.setViewCode(category.getOrg_code());
			}
		}
	}
	
}
