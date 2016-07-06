/**
 * 
 */
package com.yonyou.bpm.engine.query.sysdefault;

import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.bpm.engine.query.BaseListQuery;

/**
 * @author chouhl
 *
 */
public class SysDefUserGroupListQuery extends BaseListQuery {
	
	protected IdentityService identityService;
	
	public SysDefUserGroupListQuery(IdentityService identityService){
		this.identityService = identityService;
	}
	
	@Override
	public ObjectNode loadData(String pk, Map<String, Object> queryConditions) {
		GroupQuery query = identityService.createGroupQuery();
		
		query.groupId(pk);
		
		Group group = query.singleResult();
		
		ObjectNode result = getMapper().createObjectNode();
		
		result = this.changeObjectToObjectNode(group, result, queryConditions);
		
		return result;
	}
	
	@Override
	public ObjectNode loadDatas(int start, int count, String searchText, Map<String, Object> queryConditions) {
		GroupQuery query = identityService.createGroupQuery();
		
		if(!this.isBlank(searchText)){
			query.groupNameLike("%" + searchText +"%");
		}
		
		query.orderByGroupId().asc();
		
		ObjectNode result = getMapper().createObjectNode();
		
		long totalCount = query.count();
		result.put(KEY_TOTAL_COUNT, totalCount);
		
		List<Group> list = query.listPage(start, count);
		result = this.changeListToObjectNode(list, result, queryConditions);
		
		return result;
	}

	@Override
	protected <T> ObjectNode getNeedDatas(T t, ObjectNode node) {
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(t != null){
			Group vo = (Group)t;
			
			node.put(KEY_PK, vo.getId());
			node.put(KEY_CODE, vo.getId());
			node.put(KEY_NAME, vo.getName());
			
			node.put(KEY_ID, vo.getId());
			node.put(KEY_PID, "");
			
			node.put(KEY_NODE_TYPE, Group.class.getSimpleName());
		}
		
		return node;
	}
	
}
