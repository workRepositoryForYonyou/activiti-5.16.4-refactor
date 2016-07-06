package com.yonyou.bpm.core.agent;

import java.util.List;
import java.util.Map;

import com.yonyou.bpm.core.impl.AgentQueryParam;
/**
 * 代理人设置
 * @author zhaohb
 *
 */
public interface AgentService {
	
	int save(Agent agent)throws Exception ;
	
	int delete(Agent agent)throws Exception ;
	
	Agent getAgentById(String id);
	
	List<? extends Agent> findAllAgent();
	
	List<? extends Agent> query(AgentQueryParam agentQueryParam);
	
	long count(AgentQueryParam agentQueryParam);
	
	
	String getAgentUserId(String userId,Map<String,String> others);
	
	
	
	

}
