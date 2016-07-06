package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.activiti.engine.impl.DeploymentQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.repository.Deployment;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

public class BpmDeploymentQueryImpl extends DeploymentQueryImpl implements CategoryQueryParam<BpmDeploymentQueryImpl> {

	private static final long serialVersionUID = 1L;
	protected Set<String> categoryIds;

	public BpmDeploymentQueryImpl() {
	}

	public BpmDeploymentQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmDeploymentQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmDeploymentQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	private boolean isExtend() {
		return (categoryIds != null && categoryIds.size() > 0);
	}

	@Override
	public long executeCount(CommandContext commandContext) {
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		checkQueryOk();
		final String query = "bpmSelectDeploymentCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getDeploymentEntityManager()
					.findDeploymentCountByQueryCriteria(this);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deployment> executeList(CommandContext commandContext, Page page) {
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		checkQueryOk();
		final String query = "bpmSelectDeploymentsByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return commandContext.getDbSqlSession().selectList(query, this,
					page);
		} else {
			return commandContext.getDeploymentEntityManager()
					.findDeploymentsByQueryCriteria(this, page);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmDeployment.xml";
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
