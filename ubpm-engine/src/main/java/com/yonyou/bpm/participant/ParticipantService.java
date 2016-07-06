package com.yonyou.bpm.participant;

import java.util.List;
import java.util.Map;

import com.yonyou.bpm.participant.adapter.ParticipantAdapter;
import com.yonyou.bpm.participant.config.ParticipantConfig;
import com.yonyou.bpm.participant.config.ParticipantFilterConfig;
import com.yonyou.bpm.participant.context.ParticipantContext;
/**
 * 参与者对外服务接口
 * @author zhaohb
 *
 */
public interface ParticipantService {
	/**
	 * 获取参与者配置
	 * @return
	 */
	Map<String,	ParticipantConfig> getParticipantConfigs();
	/**
	 * 获取参与者过滤器配置
	 * @return
	 */
	Map<String,	ParticipantFilterConfig> getParticipantFilterConfigs();
	/**
	 * 获取参与者
	 * @param participantContext
	 * @return
	 */
	List<Participant> getParticipants(ParticipantContext participantContext);
	/**
	 * 参与者是否包含id，限定未考虑
	 * @param participantContext
	 * @param id
	 * @return
	 */
	boolean contains(ParticipantContext participantContext,String id);
	/**
	 * 查找用接口
	 * @return
	 */
	ParticipantAdapter getUserAdapter();
	void setUserAdapter(ParticipantAdapter userAdapter);

}
