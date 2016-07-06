package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.activiti.engine.impl.ModelQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.repository.Model;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

public class BpmModelQueryImpl extends ModelQueryImpl implements CategoryQueryParam<BpmModelQueryImpl>{

	private static final long serialVersionUID = 1L;
	protected Set<String> categoryIds;

	public BpmModelQueryImpl() {
	}

	public BpmModelQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmModelQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmModelQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	private boolean isExtend() {
		return (categoryIds != null && categoryIds.size() > 0);
	}

	// results ////////////////////////////////////////////

	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectModelCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getModelEntityManager()
					.findModelCountByQueryCriteria(this);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Model> executeList(CommandContext commandContext, Page page) {
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectModelsByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return commandContext.getDbSqlSession().selectList(query, this,
					page);
		} else {
			return commandContext.getModelEntityManager()
					.findModelsByQueryCriteria(this, page);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmModel.xml";
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
