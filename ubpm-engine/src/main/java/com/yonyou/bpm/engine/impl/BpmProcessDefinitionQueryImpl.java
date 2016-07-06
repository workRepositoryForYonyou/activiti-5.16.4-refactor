package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.ProcessDefinitionQueryImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

/**
 * 流程定义查询扩展
 * 
 * @author zhaohb
 *
 */
public class BpmProcessDefinitionQueryImpl extends ProcessDefinitionQueryImpl implements CategoryQueryParam<BpmProcessDefinitionQueryImpl> {

	private static final long serialVersionUID = 1L;

	protected List<String> deploymentIds;
	protected Set<String> categoryIds;
	protected Set<String> applicationIds;
	protected String applicationId;

	public BpmProcessDefinitionQueryImpl() {
	}

	public BpmProcessDefinitionQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmProcessDefinitionQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmProcessDefinitionQueryImpl deploymentIds(
			List<String> deploymentIds) {
		this.deploymentIds = deploymentIds;
		return this;
	}

	public BpmProcessDefinitionQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	public BpmProcessDefinitionQueryImpl applicationIds(
			Set<String> applicationIds) {
		this.applicationIds = applicationIds;
		return this;
	}

	public BpmProcessDefinitionQueryImpl applicationId(String applicationId) {
		this.applicationId = applicationId;
		return this;
	}

	private boolean isExtend() {
		return (categoryIds != null && categoryIds.size() > 0)
				|| (applicationIds != null && applicationIds.size() > 0)
				|| (deploymentIds != null && deploymentIds.size() > 0)
				|| applicationId != null;
	}

	// results ////////////////////////////////////////////

	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectProcessDefinitionCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getProcessDefinitionEntityManager()
					.findProcessDefinitionCountByQueryCriteria(this);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProcessDefinition> executeList(CommandContext commandContext,
			Page page) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectProcessDefinitionsByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return commandContext.getDbSqlSession().selectList(query, this,
					page);
		} else {
			return commandContext.getProcessDefinitionEntityManager()
					.findProcessDefinitionsByQueryCriteria(this, page);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmProcessDefinition.xml";
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
