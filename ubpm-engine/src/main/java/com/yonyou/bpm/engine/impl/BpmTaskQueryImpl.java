package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.TaskQueryImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.task.Task;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

/**
 * 任务查询
 * 
 * @author zhaohb
 *
 */
public class BpmTaskQueryImpl extends TaskQueryImpl implements CategoryQueryParam<BpmTaskQueryImpl>{
	private static final long serialVersionUID = 1L;

	protected Set<String> categoryIds;

	public BpmTaskQueryImpl() {
	}

	public BpmTaskQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmTaskQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmTaskQueryImpl(CommandExecutor commandExecutor, String databaseType) {
		super(commandExecutor, databaseType);
	}

	public BpmTaskQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}

	/**
	 * 在办的任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl inDoing() {
		includeTaskLocalVariables();
		variableValueEquals("inDoing", Boolean.TRUE, true);
		return this;
	}

	/**
	 * 产生加签的任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl onlyCounterSigning() {
		includeTaskLocalVariables();
		variableValueEquals("counterSigning", Boolean.TRUE, true);
		return this;
	}

	/**
	 * 加签产生的任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl onlyCounterSignCreated() {
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
	public BpmTaskQueryImpl onlyCounterSignCreatedParrallel() {
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
	public BpmTaskQueryImpl onlyCounterSignCreatedSequence() {
		includeTaskLocalVariables();
		// sequence parrallel
		variableValueLike("createType", "countSignSequence", true);
		return this;
	}

	/**
	 * 已经创建协办任务的任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl assisting() {
		includeTaskLocalVariables();
		variableValueEquals("assistingConfirm", Boolean.FALSE, true);
		return this;
	}

	/**
	 * 协办任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl assistCreated() {
		includeTaskLocalVariables();
		variableValueEquals("createType", "assist", true);
		return this;
	}

	/**
	 * 需要协办确认的任务
	 * 
	 * @return
	 */
	public BpmTaskQueryImpl assistConfirm() {
		includeTaskLocalVariables();
		variableValueEquals("assistingConfirm", Boolean.TRUE, true);
		return this;
	}

	private boolean isExtend() {
		return (categoryIds != null && categoryIds.size() > 0);
	}

	// results //////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public List<Task> executeList(CommandContext commandContext, Page page) {
		ensureVariablesInitialized();
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectTaskByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			if (includeTaskLocalVariables || includeProcessVariables) {
				return findTasksAndVariablesByQueryCriteria(this,
						commandContext);
			} else {
				return commandContext.getDbSqlSession().selectList(query, this,
						page);
			}
		} else {
			if (includeTaskLocalVariables || includeProcessVariables) {
				return commandContext.getTaskEntityManager()
						.findTasksAndVariablesByQueryCriteria(this);
			} else {
				return commandContext.getTaskEntityManager()
						.findTasksByQueryCriteria(this);
			}
		}

	}

	public long executeCount(CommandContext commandContext) {
		ensureVariablesInitialized();
		checkQueryOk();
		SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectTaskCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext.getTaskEntityManager()
					.findTaskCountByQueryCriteria(this);
		}
	}

	private void addResource(Configuration configuration) {
		String resource = "com/yonyou/bpm/entity/BpmTask.xml";
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
	public List<Task> findTasksAndVariablesByQueryCriteria(
			TaskQueryImpl taskQuery, CommandContext commandContext) {
		final String query = "selectTaskWithVariablesByQueryCriteria";
		// paging doesn't work for combining task instances and variables due to
		// an outer join, so doing it in-memory
		if (taskQuery.getFirstResult() < 0 || taskQuery.getMaxResults() <= 0) {
			return Collections.EMPTY_LIST;
		}

		int firstResult = taskQuery.getFirstResult();
		int maxResults = taskQuery.getMaxResults();

		// setting max results, limit to 20000 results for performance reasons
		taskQuery.setMaxResults(20000);
		taskQuery.setFirstResult(0);

		List<Task> instanceList = commandContext.getDbSqlSession()
				.selectListWithRawParameterWithoutFilter(query, taskQuery,
						taskQuery.getFirstResult(), taskQuery.getMaxResults());

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
		return Collections.EMPTY_LIST;
	}

}
