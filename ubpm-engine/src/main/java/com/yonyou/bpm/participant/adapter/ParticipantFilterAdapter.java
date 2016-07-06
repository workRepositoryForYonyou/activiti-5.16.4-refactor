package com.yonyou.bpm.participant.adapter;

import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 过滤器
 * @author zhaohb
 *
 */
public interface ParticipantFilterAdapter {
	
	List<Participant> filter(List<Participant> findResult,ParticipantContext participantContext);

}
