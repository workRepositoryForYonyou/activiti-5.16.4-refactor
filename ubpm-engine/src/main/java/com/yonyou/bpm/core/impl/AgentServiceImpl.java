package com.yonyou.bpm.core.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.yonyou.bpm.core.agent.Agent;
import com.yonyou.bpm.core.agent.AgentService;
import com.yonyou.bpm.core.dao.BaseDao;
import com.yonyou.bpm.core.entity.AgentEntity;
/**
 * 代理人服务默认实现
 * @author zhaohb
 *
 */
public class AgentServiceImpl implements AgentService{
	private static Logger logger = LoggerFactory
			.getLogger(AgentServiceImpl.class);
	private static final String RESOURCE="com/yonyou/bpm/entity/agent.xml";
	private SqlSessionFactory sqlSessionFactory;
	
	private CommandExecutor commandExecutor;
	
	public AgentServiceImpl(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
		initMappingResource();
	}
	protected void initMappingResource(){
			if(!this.sqlSessionFactory.getConfiguration().getMappedStatementNames().contains("insertBpmAgent")){
				BaseDao baseDao=new BaseDao(this.sqlSessionFactory);
				this.sqlSessionFactory=baseDao.addClassResource(RESOURCE).getSqlSessionFactory();
			}
	}
	
	@Override
	public int save(final Agent agent) throws Exception {
		if (agent.getId() == null || "".equals(agent.getId().trim())) {
			if (agent instanceof AgentEntity) {
				final AgentEntity agentEntity = (AgentEntity) agent;
				String id = new UUIDGenerator().generateId(agentEntity)
						.toString();
				agentEntity.setCreateTime(new Date());
				agentEntity.setId(id);
				return commandExecutor.execute(new Command<Integer>() {
					@Override
					public Integer execute(CommandContext commandContext) {
						return sqlSessionFactory.openSession().insert(
								"insertBpmAgent", agentEntity);
					}
				});
			}

		} else {
			if (agent instanceof AgentEntity) {
				AgentEntity agentEntity = (AgentEntity) agent;
				agentEntity.setModifyTime(new Date());
			}
			return commandExecutor.execute(new Command<Integer>() {
				@Override
				public Integer execute(CommandContext commandContext) {
					return sqlSessionFactory.openSession().update(
							"updateBpmAgent", agent);
				}
			});
		}
		return 0;
	}

	@Override
	public int delete(final Agent agent) throws Exception {
		return commandExecutor.execute(new Command<Integer>() {
			@Override
			public Integer execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().delete("deleteBpmAgent", agent);
			}
		});
	}


	@Override
	public Agent getAgentById(final String id) {
		return commandExecutor.execute(new Command<Agent>() {
			@Override
			public Agent execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectOne("selectBpmAgentById", id);
			}
		});
	}

	@Override
	public List<? extends Agent> findAllAgent() {
		AgentQueryParam agentQueryParam=new AgentQueryParam();
		return query(agentQueryParam);
	}
	public List<? extends Agent> query(final AgentQueryParam agentQueryParam){
		final RowBounds rowBounds=new RowBounds(agentQueryParam.getFirstResult(), agentQueryParam.getMaxResults());
		return commandExecutor.execute(new Command<List<? extends Agent>>() {
			@Override
			public List<? extends Agent> execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectList("selectBpmAgents", agentQueryParam, rowBounds);
			}
		});
		
	}
	
	public long count(final AgentQueryParam agentQueryParam){
			List<Object> list=null;
			list= commandExecutor.execute(new Command<List<Object>>() {
				@Override
				public List<Object> execute(
						CommandContext commandContext) {
					return sqlSessionFactory.openSession().selectList("selectBpmAgents", agentQueryParam);
				}
			});
			return list==null?0:list.size();
	}
	@Override
	public String getAgentUserId(String userId, Map<String, String> others) {
		final AgentQueryParam agentQueryParam=new AgentQueryParam();
		agentQueryParam.userId(userId);
		agentQueryParam.enable();
		Agent agent=null;
		
		agent=commandExecutor.execute(new Command<Agent>() {
			@Override
			public Agent execute(
					CommandContext commandContext) {
				return sqlSessionFactory.openSession().selectOne("selectBpmAgents", agentQueryParam);
			}
		});
		
		if(agent==null){
			return null;
		}
		return agent.getAgentUserId();
	}
	public CommandExecutor getCommandExecutor() {
		return commandExecutor;
	}
	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

}
