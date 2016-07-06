package com.yonyou.bpm.core.assign;

import java.io.Serializable;

import com.yonyou.bpm.participant.Participant;
/**
 * 指派详细信息
 * @author zhaohb
 *
 */
public class AssignInfoItem implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//活动Id
	private String activityId;
	//活动名称
	private String activityName;
	//参与者
	private Participant[] participants;
	//历史参与者
	private Participant[] hisParticipants;
	
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public Participant[] getParticipants() {
		return participants;
	}
	public void setParticipants(Participant[] participants) {
		this.participants = participants;
	}
	public Participant[] getHisParticipants() {
		return hisParticipants;
	}
	public void setHisParticipants(Participant[] hisParticipants) {
		this.hisParticipants = hisParticipants;
	}
}
