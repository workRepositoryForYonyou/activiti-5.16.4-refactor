package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

/**
 * 历史环节信息查询
 * 
 * @author zhaohb
 *
 */
public class BpmHistoricActivityInstanceQueryImpl extends
		HistoricActivityInstanceQueryImpl implements CategoryQueryParam<BpmHistoricActivityInstanceQueryImpl>{

	private static final long serialVersionUID = 1L;

	protected String deploymentId;
	protected List<String> deploymentIds;

	protected Set<String> categoryIds;

	public BpmHistoricActivityInstanceQueryImpl() {
	}

	public BpmHistoricActivityInstanceQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmHistoricActivityInstanceQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmHistoricActivityInstanceQueryImpl deploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
		return this;
	}

	public BpmHistoricActivityInstanceQueryImpl deploymentIds(
			List<String> deploymentIds) {
		this.deploymentIds = deploymentIds;
		return this;
	}

	public BpmHistoricActivityInstanceQueryImpl categoryIds(
			Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	private boolean isExtend() {
		return (deploymentIds != null && deploymentIds.size() > 0)
				|| (categoryIds != null && categoryIds.size() > 0)
				|| deploymentId != null;
	}

	@Override
	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricActivityInstanceCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getHistoricActivityInstanceEntityManager()
					.findHistoricActivityInstanceCountByQueryCriteria(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricActivityInstance> executeList(
			CommandContext commandContext, Page page) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricActivityInstancesByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return commandContext.getDbSqlSession().selectList(query, this,
					page);
		} else {
			return commandContext.getHistoricActivityInstanceEntityManager()
					.findHistoricActivityInstancesByQueryCriteria(this, page);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmHistoricActivityInstance.xml";
		ClassPathResource classPathResource = new ClassPathResource(resource);
		XMLMapperBuilder mapperParser = null;
		try {
			mapperParser = new XMLMapperBuilder(
					classPathResource.getInputStream(), configuration,
					classPathResource.toString(),
					configuration.getSqlFragments());
		} catch (IOException e) {
			throw new BpmException(e);
		}
		mapperParser.parse();
	}

}
