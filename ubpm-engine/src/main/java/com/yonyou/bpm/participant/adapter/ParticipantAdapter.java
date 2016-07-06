package com.yonyou.bpm.participant.adapter;

import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 参与者查找实现
 * ？是否需要提供函数形式的？
 * "#user(userPks),#group(groupPks)"
 * @author zhaohb
 *
 */
public interface ParticipantAdapter {
	
	List<Participant> find(ProcessParticipantItem processParticipantItem,ParticipantContext participantContext);
	
	boolean contains(ProcessParticipantItem processParticipantItem,ParticipantContext participantContext,String id);
	
}
