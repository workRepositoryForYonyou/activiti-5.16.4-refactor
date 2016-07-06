package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.HistoricProcessInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

public class BpmHistoricProcessInstanceQueryImpl extends
		HistoricProcessInstanceQueryImpl implements CategoryQueryParam<BpmHistoricProcessInstanceQueryImpl>{

	private static final long serialVersionUID = 1L;

	protected List<String> startedBys;

	protected Set<String> categoryIds;

	public BpmHistoricProcessInstanceQueryImpl() {
	}

	public BpmHistoricProcessInstanceQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmHistoricProcessInstanceQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmHistoricProcessInstanceQueryImpl startedBys(
			List<String> startedBys) {
		this.startedBys = startedBys;
		return this;
	}

	public BpmHistoricProcessInstanceQueryImpl categoryIds(
			Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	// results //////////////////////////////////////////

	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		ensureVariablesInitialized();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricProcessInstanceCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getHistoricProcessInstanceEntityManager()
					.findHistoricProcessInstanceCountByQueryCriteria(this);
		}

	}

	private boolean isExtend() {
		return (startedBys != null && startedBys.size() > 0)
				|| (categoryIds != null && categoryIds.size() > 0);
	}

	@SuppressWarnings("unchecked")
	public List<HistoricProcessInstance> executeList(
			CommandContext commandContext, Page page) {
		checkQueryOk();
		ensureVariablesInitialized();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricProcessInstancesByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			if (includeProcessVariables) {
				return findHistoricProcessInstancesAndVariablesByQueryCriteria(
						this, commandContext);
			} else {

				return commandContext.getDbSqlSession().selectList(query, this,
						page);
			}
		} else {
			if (includeProcessVariables) {
				return commandContext
						.getHistoricProcessInstanceEntityManager()
						.findHistoricProcessInstancesAndVariablesByQueryCriteria(
								this);
			} else {
				return commandContext.getHistoricProcessInstanceEntityManager()
						.findHistoricProcessInstancesByQueryCriteria(this);
			}
		}

	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmHistoricProcessInstance.xml";
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

	@SuppressWarnings("unchecked")
	public List<HistoricProcessInstance> findHistoricProcessInstancesAndVariablesByQueryCriteria(
			HistoricProcessInstanceQueryImpl historicProcessInstanceQuery,
			CommandContext commandContext) {
		if (commandContext.getHistoryManager().isHistoryEnabled()) {
			// paging doesn't work for combining process instances and variables
			// due to an outer join, so doing it in-memory
			if (historicProcessInstanceQuery.getFirstResult() < 0
					|| historicProcessInstanceQuery.getMaxResults() <= 0) {
				return Collections.EMPTY_LIST;
			}

			int firstResult = historicProcessInstanceQuery.getFirstResult();
			int maxResults = historicProcessInstanceQuery.getMaxResults();

			// setting max results, limit to 20000 results for performance
			// reasons
			historicProcessInstanceQuery.setMaxResults(20000);
			historicProcessInstanceQuery.setFirstResult(0);

			List<HistoricProcessInstance> instanceList = commandContext
					.getDbSqlSession()
					.selectListWithRawParameterWithoutFilter(
							"bpmSelectHistoricProcessInstancesWithVariablesByQueryCriteria",
							historicProcessInstanceQuery,
							historicProcessInstanceQuery.getFirstResult(),
							historicProcessInstanceQuery.getMaxResults());

			if (instanceList != null && !instanceList.isEmpty()) {
				if (firstResult > 0) {
					if (firstResult <= instanceList.size()) {
						int toIndex = firstResult
								+ Math.min(maxResults, instanceList.size()
										- firstResult);
						return instanceList.subList(firstResult, toIndex);
					} else {
						return Collections.EMPTY_LIST;
					}
				} else {
					int toIndex = Math.min(maxResults, instanceList.size());
					return instanceList.subList(0, toIndex);
				}
			}
		}
		return Collections.EMPTY_LIST;
	}

}
