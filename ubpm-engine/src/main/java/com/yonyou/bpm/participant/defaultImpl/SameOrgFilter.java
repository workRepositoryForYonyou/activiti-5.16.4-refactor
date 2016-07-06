package com.yonyou.bpm.participant.defaultImpl;

import java.util.List;

import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.adapter.AbstractParticipantFilterAdapter;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 同组织限定
 * @author zhaohb
 *
 */
public class SameOrgFilter extends AbstractParticipantFilterAdapter{

	@Override
	public List<Participant> filter(List<Participant> findResult,
			ParticipantContext participantContext) {
		return findResult;
	}

}
