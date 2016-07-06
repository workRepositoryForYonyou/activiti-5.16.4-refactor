package com.yonyou.bpm.engine.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.ProcessInstanceQueryImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.category.CategoryQueryParam;

public class BpmProcessInstanceQueryImpl extends ProcessInstanceQueryImpl implements CategoryQueryParam<BpmProcessInstanceQueryImpl>{

	private static final long serialVersionUID = 1L;

	protected Set<String> categoryIds;
	
	public BpmProcessInstanceQueryImpl() {
	}

	public BpmProcessInstanceQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public BpmProcessInstanceQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public BpmProcessInstanceQueryImpl categoryIds(Set<String> categoryIds) {
		this.categoryIds = categoryIds;
		return this;
	}
	
	private boolean isExtend() {
		return (categoryIds != null && categoryIds.size() > 0);
	}
	//results /////////////////////////////////////////////////////////////////
	  
	  public long executeCount(CommandContext commandContext) {
	    checkQueryOk();
	    ensureVariablesInitialized();
	    
	    SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectProcessInstanceCountByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			return (Long) commandContext.getDbSqlSession().selectOne(query,
					this);
		} else {
			return commandContext
				      .getExecutionEntityManager()
				      .findProcessInstanceCountByQueryCriteria(this);
		}
	   
	  }

	  @SuppressWarnings("unchecked")
	public List<ProcessInstance> executeList(CommandContext commandContext, Page page) {
	    checkQueryOk();
	    ensureVariablesInitialized();
	    
	    SqlSessionFactory sqlSessionFactory = commandContext.getDbSqlSession()
				.getDbSqlSessionFactory().getSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		final String query = "bpmSelectProcessInstanceByQueryCriteria";
		if (!configuration.hasStatement(query)) {
			addResource(configuration);
		}
		if (isExtend()) {
			if (includeProcessVariables) {
				return findProcessInstanceAndVariablesByQueryCriteria(
						this, commandContext);
			} else {
				return commandContext.getDbSqlSession().selectList(query, this,
						page);
			}
		} else {
			 if (includeProcessVariables) {
			      return commandContext
			          .getExecutionEntityManager()
			          .findProcessInstanceAndVariablesByQueryCriteria(this);
			    } else {
			      return commandContext
			          .getExecutionEntityManager()
			          .findProcessInstanceByQueryCriteria(this);
			    }
		}
	   
	  }
	  private void addResource(Configuration configuration) {
			String resource = "com/yonyou/bpm/entity/BpmExecution.xml";
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
	  public List<ProcessInstance> findProcessInstanceAndVariablesByQueryCriteria(ProcessInstanceQueryImpl executionQuery,CommandContext commandContext) {
	    // paging doesn't work for combining process instances and variables due to an outer join, so doing it in-memory
	    if (executionQuery.getFirstResult() < 0 || executionQuery.getMaxResults() <= 0) {
	      return Collections.EMPTY_LIST;
	    }
	    
	    int firstResult = executionQuery.getFirstResult();
	    int maxResults = executionQuery.getMaxResults();
	    
	    // setting max results, limit to 20000 results for performance reasons
	    executionQuery.setMaxResults(20000);
	    executionQuery.setFirstResult(0);
	    
	    List<ProcessInstance> instanceList = commandContext.getDbSqlSession().selectListWithRawParameterWithoutFilter("selectProcessInstanceWithVariablesByQueryCriteria", 
	        executionQuery, executionQuery.getFirstResult(), executionQuery.getMaxResults());
	    
	    if (instanceList != null && !instanceList.isEmpty()) {
	      if (firstResult > 0) {
	        if (firstResult <= instanceList.size()) {
	          int toIndex = firstResult + Math.min(maxResults, instanceList.size() - firstResult);
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
