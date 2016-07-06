package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

public class BpmHistoricTaskInstanceQueryImpl extends
		HistoricTaskInstanceQueryImpl implements CategoryQueryParam<HistoricTaskInstanceQueryImpl>{

	private static final long serialVersionUID = 1L;

	protected List<String> taskAssignees;

	protected boolean isOnlyCounterSigning = false;

	protected boolean isOnlyCounterSignCreated = false;

	protected Set<String> categoryIds;

	public BpmHistoricTaskInstanceQueryImpl() {
	}

	public BpmHistoricTaskInstanceQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmHistoricTaskInstanceQueryImpl(CommandExecutor commandExecutor,
			String databaseType) {
		super(commandExecutor, databaseType);
	}

	public BpmHistoricTaskInstanceQueryImpl startedBys(
			List<String> taskAssignees) {
		this.taskAssignees = taskAssignees;
		return this;
	}

	public BpmHistoricTaskInstanceQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	/**
	 * 产生加签的任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl onlyCounterSigning() {
		isOnlyCounterSigning = true;
		includeTaskLocalVariables();
		variableValueEquals("counterSigning", Boolean.TRUE, true);
		return this;
	}

	/**
	 * 加签产生的任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl onlyCounterSignCreated() {
		includeTaskLocalVariables();
		// sequence parrallel
		variableValueLike("createType", "countSign", true);
		return this;
	}

	/**
	 * 并行加签产生的任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl onlyCounterSignCreatedParrallel() {
		includeTaskLocalVariables();
		// sequence parrallel
		variableValueLike("createType", "countSignParrallel", true);
		return this;
	}

	/**
	 * 串行加签产生的任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl onlyCounterSignCreatedSequence() {
		includeTaskLocalVariables();
		// sequence parrallel
		variableValueLike("createType", "countSignSequence", true);
		return this;
	}

	/**
	 * 协办任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl assistCreated() {
		includeTaskLocalVariables();
		// sequence parrallel
		variableValueLike("createType", "assist", true);
		return this;
	}

	/**
	 * 协办人完成的协办任务
	 * 
	 * @return
	 */
	public BpmHistoricTaskInstanceQueryImpl assistCompleted() {
		includeTaskLocalVariables();
		variableValueEquals("assistCompleted", Boolean.TRUE, true);
		return this;
	}

	private boolean isExtend() {
		return (taskAssignees != null && taskAssignees.size() > 0)
				|| (categoryIds != null && categoryIds.size() > 0)
				||unfinished||finished;
	}

	// results //////////////////////////////////////////
	@Override
	public long executeCount(CommandContext commandContext) {
		ensureVariablesInitialized();
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricTaskInstanceCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getHistoricTaskInstanceEntityManager()
					.findHistoricTaskInstanceCountByQueryCriteria(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricTaskInstance> executeList(
			CommandContext commandContext, Page page) {
		ensureVariablesInitialized();
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectHistoricTaskInstancesByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			if (includeTaskLocalVariables || includeProcessVariables) {
				return findHistoricTaskInstancesAndVariablesByQueryCriteria(
						this, commandContext);
			} else {
				return commandContext.getDbSqlSession().selectList(query, this,
						page);
			}
		} else {
			if (includeTaskLocalVariables || includeProcessVariables) {
				return commandContext.getHistoricTaskInstanceEntityManager()
						.findHistoricTaskInstancesAndVariablesByQueryCriteria(
								this);
			} else {
				return commandContext.getHistoricTaskInstanceEntityManager()
						.findHistoricTaskInstancesByQueryCriteria(this);
			}
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmHistoricTaskInstance.xml";
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
	public List<HistoricTaskInstance> findHistoricTaskInstancesAndVariablesByQueryCriteria(
			HistoricTaskInstanceQueryImpl historicTaskInstanceQuery,
			CommandContext commandContext) {
		if (commandContext.getHistoryManager().isHistoryEnabled()) {
			// paging doesn't work for combining task instances and variables
			// due to an outer join, so doing it in-memory
			if (historicTaskInstanceQuery.getFirstResult() < 0
					|| historicTaskInstanceQuery.getMaxResults() <= 0) {
				return Collections.EMPTY_LIST;
			}

			int firstResult = historicTaskInstanceQuery.getFirstResult();
			int maxResults = historicTaskInstanceQuery.getMaxResults();

			// setting max results, limit to 20000 results for performance
			// reasons
			historicTaskInstanceQuery.setMaxResults(20000);
			historicTaskInstanceQuery.setFirstResult(0);

			List<HistoricTaskInstance> instanceList = commandContext
					.getDbSqlSession()
					.selectListWithRawParameterWithoutFilter(
							"selectHistoricTaskInstancesWithVariablesByQueryCriteria",
							historicTaskInstanceQuery,
							historicTaskInstanceQuery.getFirstResult(),
							historicTaskInstanceQuery.getMaxResults());

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
