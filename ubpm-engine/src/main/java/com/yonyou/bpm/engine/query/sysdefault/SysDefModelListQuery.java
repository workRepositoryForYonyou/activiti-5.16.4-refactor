/**
 * 
 */
package com.yonyou.bpm.engine.query.sysdefault;

import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.bpm.engine.query.BaseListQuery;

/**
 * @author chouhl
 *
 */
public class SysDefModelListQuery extends BaseListQuery {
	
	protected RepositoryService service;

	public SysDefModelListQuery(RepositoryService service) {
		this.service = service;
	}
	
	@Override
	public ObjectNode loadDatas(int start, int count, String searchText, Map<String, Object> queryConditions) {
		ModelQuery query = this.service.createModelQuery();
		
		if(!this.isBlank(searchText)){
			query.modelNameLike("%" + searchText + "%");
		}
		
		query.orderByLastUpdateTime().desc();
		
		ObjectNode result = getMapper().createObjectNode();
		
		long totalCount = query.count();
		result.put(KEY_TOTAL_COUNT, totalCount);
		
		List<Model> list = query.listPage(start, count);
		result = this.changeListToObjectNode(list, result, queryConditions);
		
		return result;
	}
	
	@Override
	public ObjectNode loadData(String pk, Map<String, Object> queryConditions) {
		ModelQuery query = this.service.createModelQuery();
		
		query.modelId(pk);
		
		Model model = query.singleResult();
		
		ObjectNode result = getMapper().createObjectNode();
		
		result = this.changeObjectToObjectNode(model, result, queryConditions);
		
		return result;
	}
	
	@Override
	protected <T> ObjectNode getNeedDatas(T t, ObjectNode node) {
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(t != null){
			Model vo = (Model)t;
			
			node.put(KEY_PK, vo.getId());
			node.put(KEY_CODE, vo.getId());
			node.put(KEY_NAME, vo.getName());
			
			node.put(KEY_ID, vo.getId());
			node.put(KEY_PID, "");
			
			node.put(KEY_NODE_TYPE, Model.class.getSimpleName());
		}
		
		return node;
	}
	
}
