package com.yonyou.bpm.utils;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.context.ExecutionContext;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.core.assign.AssignInfoItem;
import com.yonyou.bpm.participant.Participant;

public class AssignInfoUtils {
	@SuppressWarnings("rawtypes")
	public static List<String> getRuntimeAssignUser() {
		Command command =  Context.getCommandContext().getCommand();
		if (command == null)
			return null;
		AssignInfo assignInfo = null;
		Object assigninfoParam=Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_PARAM);
		if(assigninfoParam==null||!(assigninfoParam instanceof AssignInfo)){
			return null;
		}
		assignInfo=(AssignInfo)assigninfoParam;
		
		List<String> list = new ArrayList<String>();
		AssignInfoItem[] assignInfoItems = assignInfo.getAssignInfoItems();
		if (assignInfoItems == null || assignInfoItems.length == 0)
			return list;
		ExecutionContext executionContext = Context.getExecutionContext();
		ActivityImpl  activityImpl=executionContext.getExecution().getActivity();
		
		AssignInfoItem assignInfoItemResult = null;
		//获取当前活动对应的指派信息
		//获取当前活动对应的指派信息
		
		for (AssignInfoItem assignInfoItem : assignInfoItems) {
			if(assignInfoItem.getActivityId()==null||"".equals(assignInfoItem.getActivityId().trim())){
				throw new BpmException("指派信息活动ID不能为空！");
			}
			if (activityImpl.getId().equals(
					assignInfoItem.getActivityId())) {
				assignInfoItemResult = assignInfoItem;
				break;
			}
		}
	
	
		if(assignInfoItemResult==null)return null;
		Participant[] participants = assignInfoItemResult.getParticipants();
		if (participants == null || participants.length == 0)
			return list;

		
		for (Participant participant : participants) {
			list.add(participant.getId());
		}
		return list;
	}

	public static boolean assignTarget(Command command,
			TransitionImpl transitionImpl, ExecutionEntity executionEntity) {
		if (command instanceof  com.yonyou.bpm.engine.cmd.CompleteTaskCmd) {
			ActivityImpl destination = transitionImpl.getDestination();
			// 需要判断这目标是否可指派
			if(!AssignInfoUtils.isAssignAble(destination))return true;
			
			AssignInfo assignInfo =(AssignInfo)Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_PARAM);
			if (assignInfo == null) {
				return true;
			}
			return AssignInfoUtils.hasAssignInfoItemOnTransition(assignInfo,
					transitionImpl);

			// 发起流程时指派
		} else if (command instanceof  com.yonyou.bpm.engine.cmd.StartProcessInstanceCmd) {
			ExecutionContext executionContext = Context.getExecutionContext();
			if (executionEntity == null) {
				executionEntity = executionContext.getExecution();
			}
			ActivityImpl destination = transitionImpl.getDestination();
			if(!AssignInfoUtils.isAssignAble(destination))return true;
			
			AssignInfo assignInfo =(AssignInfo)Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_PARAM);
			if (assignInfo == null) {
				return true;
			}
			return AssignInfoUtils.hasAssignInfoItemOnTransition(assignInfo,
					transitionImpl);
		}
		return true;
	}
	public static boolean isAssignAble(PvmActivity activityImpl){
		boolean isAssignAble = false;
		String actType = (String) activityImpl.getProperty("type");
		List<PvmTransition> incomingTransitions=activityImpl.getIncomingTransitions();
		//并行网关不允许指派
		if(incomingTransitions!=null&&incomingTransitions.size()>0){
			for (PvmTransition pvmTransition : incomingTransitions) {
				if(BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL.equals(pvmTransition.getSource().getProperty("type"))){
					return false;
				}
			}
		}
		if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(actType) || "".equals(actType)) {
			if(activityImpl.getProperty("assignAble")!=null){
				isAssignAble=Boolean.valueOf((String)activityImpl.getProperty("assignAble"));
			}
		}
		return isAssignAble;
	}
	public static boolean canntAssignOnTransition(
			TransitionImpl transitionImpl, AssignInfo assignInfo) {
		ActivityImpl destination = transitionImpl.getDestination();
		if (!BpmnXMLConstants.ELEMENT_TASK_USER.equals(destination.getProperty("type"))) {
			return true;
		}
		if (assignInfo == null) {
			return true;
		}
		return false;
	}

	public static boolean hasAssignInfoItemOnTransition(AssignInfo assignInfo,
			TransitionImpl transitionImpl) {
		if (assignInfo == null) {
			throw new IllegalArgumentException("指派信息不能为空！");
		}
		AssignInfoItem[] assignInfoItems = assignInfo.getAssignInfoItems();
		if (assignInfoItems == null || assignInfoItems.length == 0) {
			throw new IllegalArgumentException("指派信息中详细信息不能为空！");
		}
		for (AssignInfoItem assignInfoItem : assignInfoItems) {
			String targetId = assignInfoItem.getActivityId();
			if (targetId == null || "".equals(targetId)) {
				throw new IllegalArgumentException("指派信息中活动ID不能为空！");
			}
			String destinationId = transitionImpl.getDestination().getId();
			if (targetId.equals(destinationId)) {
				return true;
			}
		}
		return false;
	}

}
