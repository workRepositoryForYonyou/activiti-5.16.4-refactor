package com.yonyou.bpm.participant.adapter;

import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 参与则过滤实现需要继承的基类
 * @author zhaohb
 *
 */
public abstract class AbstractParticipantFilterAdapter implements ParticipantFilterAdapter{

	@Override
	public List<Participant> filter(List<Participant> findResult,
			ParticipantContext participantContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
