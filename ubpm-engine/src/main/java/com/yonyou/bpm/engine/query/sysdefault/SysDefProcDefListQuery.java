/**
 * 
 */
package com.yonyou.bpm.engine.query.sysdefault;

import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.bpm.engine.query.BaseListQuery;

/**
 * @author chouhl
 *
 */
public class SysDefProcDefListQuery extends BaseListQuery {
	
	protected RepositoryService service;
	
	public SysDefProcDefListQuery(RepositoryService service){
		this.service = service;
	}
	
	@Override
	public ObjectNode loadDatas(int start, int count, String searchText, Map<String, Object> queryConditions) {
		ProcessDefinitionQuery query = this.service.createProcessDefinitionQuery();
		
		if(!this.isBlank(searchText)){
			query.processDefinitionNameLike("%" + searchText + "%");
		}
		
		query.orderByDeploymentId().desc().orderByProcessDefinitionVersion().desc();
		
		ObjectNode result = getMapper().createObjectNode();
		
		long totalCount = query.count();
		result.put("totalCount", totalCount);
		
		List<ProcessDefinition> pds = query.listPage(start, count);
		result = this.changeListToObjectNode(pds, result, queryConditions);
		
		return result;
	}
	
	@Override
	public ObjectNode loadData(String pk, Map<String, Object> queryConditions) {
		ProcessDefinitionQuery query = this.service.createProcessDefinitionQuery();
		
		query.processDefinitionId(pk);
		
		ProcessDefinition pd = query.singleResult();
		
		ObjectNode result = getMapper().createObjectNode();
		
		result = this.changeObjectToObjectNode(pd, result, queryConditions);
		
		return result;
	}

	@Override
	protected <T> ObjectNode getNeedDatas(T t, ObjectNode node) {
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(t != null){
			ProcessDefinition vo = (ProcessDefinition)t;
			
			node.put(KEY_PK, vo.getId());
			node.put(KEY_CODE, vo.getKey());
			node.put(KEY_NAME, vo.getName());
			
			node.put(KEY_ID, vo.getId());
			node.put(KEY_PID, "");
			
			node.put(KEY_NODE_TYPE, ProcessDefinition.class.getSimpleName());
		}
		
		return node;
	}
	
}
