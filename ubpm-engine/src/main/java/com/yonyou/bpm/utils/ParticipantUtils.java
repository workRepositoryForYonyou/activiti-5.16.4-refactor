package com.yonyou.bpm.utils;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.context.Context;

import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ParticipantService;
import com.yonyou.bpm.participant.ProcessParticipant;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.context.ParticipantContext;
import com.yonyou.bpm.participant.context.ParticipantContextBuilder;

public class ParticipantUtils {
	/**
	 * 根据参与者配置获取用户列表
	 * @param processParticipant
	 * @return
	 */
	public static List<String> getUser(ProcessParticipant processParticipant){
		 if(processParticipant==null)return null;
		 ProcessParticipantItem[] processParticipantItems=processParticipant.getProcessParticipantItems();
		 ParticipantContext participantContext=new ParticipantContextBuilder().bindAllProcessParticipantItems(processParticipantItems);
		 ProcessEngineConfiguration processEngineConfiguration=Context.getProcessEngineConfiguration();
		 
		 ParticipantService participantService=null;
		 if(processEngineConfiguration!=null&&processEngineConfiguration instanceof BpmEngineConfiguration){
			 BpmEngineConfiguration bpmEngineConfiguration=(BpmEngineConfiguration)processEngineConfiguration;
			 participantService= bpmEngineConfiguration.getParticipantService();
		 }
		 
		 List<Participant> list=participantService.getParticipants(participantContext);
		 if(list==null||list.size()==0)return null;
		 List<String> resultList=new ArrayList<String>();
		 for (Participant participant : list) {
			 String userId=participant.getId();
			 if(userId==null||"".equals(userId.trim()))continue;
			 resultList.add(userId);
		}
		return  resultList;
	}
	public static Participant getParticipant(String id){
		if(Context.getCommandContext()==null)return null;
		Object userselected=Context.getCommandContext().getAttribute("userselected");
		if(userselected==null)return null;
		@SuppressWarnings("unchecked")
		List<Participant> list=(List<Participant>)userselected;
		if(list.size()==0)return null;
		
		for (Participant participant : list) {
			if(participant!=null&&id!=null&&id.equals(participant.getId())){
				return participant;
			}
		}
		return null;
	}
}
