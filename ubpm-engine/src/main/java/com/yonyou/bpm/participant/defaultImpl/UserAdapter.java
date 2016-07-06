package com.yonyou.bpm.participant.defaultImpl;

import java.util.ArrayList;
import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ProcessParticipantDetail;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.adapter.AbstractParticipantAdapter;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 默认用户查找
 * @author zhaohb
 *
 */
public class UserAdapter  extends AbstractParticipantAdapter{

	@Override
	public List<Participant> find(ProcessParticipantItem processParticipantItem,ParticipantContext participantContext) {
		if(processParticipantItem==null){
			throw new IllegalArgumentException("'processParticipantItem' can not be null");
		}
		
		
		ProcessParticipantDetail[] processParticipantDetails=processParticipantItem.getDetails();
		if (processParticipantDetails==null||processParticipantDetails.length==0) {
			return null;
		}
		//userid
		List<Participant> list=new ArrayList<Participant>(processParticipantDetails.length);
		for (int i=0;i<processParticipantDetails.length;i++) {
			Participant participant=new Participant(processParticipantDetails[i].getId(), processParticipantDetails[i].getCode(), processParticipantDetails[i].getName());
			list.add(participant);
		}
		return list;
	}
	//有限从context中取
	@Override
	public boolean contains(ProcessParticipantItem processParticipantItem,ParticipantContext participantContext, String id) {
		if(processParticipantItem==null){
			throw new IllegalArgumentException("'processParticipantItem' can not be null");
		}
		if(id==null||"".equals(id.trim())){
			throw new IllegalArgumentException("'id' can not be null");
		}
		ProcessParticipantDetail[] processParticipantDetails=processParticipantItem.getDetails();
		if (processParticipantDetails==null||processParticipantDetails.length==0) {
			return false;
		}
		for (ProcessParticipantDetail processParticipantDetail : processParticipantDetails) {
			if(id.equals(processParticipantDetail.getId())){
				return true;
			}
		}
		return false;
	}

}
