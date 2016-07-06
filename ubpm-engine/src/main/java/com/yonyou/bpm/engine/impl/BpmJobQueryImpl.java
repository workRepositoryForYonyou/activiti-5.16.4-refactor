package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.JobQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.Job;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;

public class BpmJobQueryImpl extends JobQueryImpl {

	private static final long serialVersionUID = 1L;

	protected List<String> deploymentIds;

	public BpmJobQueryImpl() {
	}

	public BpmJobQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmJobQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmJobQueryImpl deploymentIds(List<String> deploymentIds) {
		this.deploymentIds = deploymentIds;
		return this;
	}

	private boolean isExtend() {
		return (deploymentIds != null && deploymentIds.size() > 0);
	}

	// results //////////////////////////////////////////

	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectJobCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getJobEntityManager()
					.findJobCountByQueryCriteria(this);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Job> executeList(CommandContext commandContext, Page page) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectJobByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {

			return commandContext.getDbSqlSession().selectList(query, this,
					page);
		} else {
			return commandContext.getJobEntityManager()
					.findJobsByQueryCriteria(this, page);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmJob.xml";
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
