package com.yonyou.bpm.participant.adapter;

import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 参与者查找实现需要继承的基类
 * @author zhaohb
 *
 */
public abstract class AbstractParticipantAdapter implements ParticipantAdapter{

	@Override
	public abstract List<Participant> find(
			ProcessParticipantItem processParticipantItem,
			ParticipantContext participantContext);

	@Override
	public  boolean contains(ProcessParticipantItem processParticipantItem,
			ParticipantContext participantContext, String id){
		if(id==null||"".equals(id.trim())){
			throw new IllegalArgumentException("'id' can not be null");
		}
		List<Participant> list=find(processParticipantItem, participantContext);
		if(list==null||list.size()==0)return false;
		for (Participant participant : list) {
			if(id.equals(participant.getId())){
				return true;
			}
		}
		return false;
	}
	
}
