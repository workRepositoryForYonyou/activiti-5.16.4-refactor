/**
 * 
 */
package com.yonyou.bpm.engine.query.sysdefault;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.bpm.engine.query.BaseListQuery;

/**
 * @author chouhl
 *
 */
public class SysDefUserListQuery extends BaseListQuery {
	
	protected IdentityService identityService;
	
	public SysDefUserListQuery(IdentityService identityService){
		this.identityService = identityService;
	}
	
	@Override
	public ObjectNode loadData(String pk, Map<String, Object> queryConditions) {
		UserQuery query = identityService.createUserQuery();
		
		query.userId(pk);
		
		User user = query.singleResult();
		
		ObjectNode result = getMapper().createObjectNode();
		
		result = this.changeObjectToObjectNode(user, result, queryConditions);
		
		return result;
	}
	
	@Override
	public ObjectNode loadDatas(int start, int count, String searchText, Map<String, Object> queryConditions) {
		UserQuery query = identityService.createUserQuery();
		
		if(!this.isBlank(searchText)){
			query.userFullNameLike("%" + searchText + "%");
		}
		
		query.orderByUserFirstName().asc();
		
		ObjectNode result = getMapper().createObjectNode();
		
		long totalCount = query.count();
		result.put(KEY_TOTAL_COUNT, totalCount);
		
		List<User> list = query.listPage(start, count);
		result = this.changeListToObjectNode(list, result, queryConditions);
		
		return result;
	}
	
	@Override
	protected <T> ObjectNode changeObjectToObjectNode(T t, ObjectNode node, Map<String, Object> params) {
		node = super.changeObjectToObjectNode(t, node, params);
		
		node = this.addOrgPropertiesToNode(node, params);
		
		return node;
	}
	
	protected ObjectNode addOrgPropertiesToNode(ObjectNode node, Map<String, Object> params) {
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		String nodeType = null;
		if(node.get(KEY_NODE_TYPE) != null){
			nodeType = node.get(KEY_NODE_TYPE).asText();
		}
		
		if(params != null && params.size() > 0 && nodeType != null){
			Iterator<String> keys = params.keySet().iterator();
			
			while(keys.hasNext()){
				String key = keys.next();
				if(!this.isBlank(key)){
					if(key.startsWith("UserNodeProperty_") && "user".equalsIgnoreCase(nodeType)){
						node.put(key.replace("UserNodeProperty_", ""), getMapper().valueToTree(params.get(key)));
					}
				}
			}
		}
		
		return node;
	}
	
	@Override
	protected <T> ObjectNode getNeedDatas(T t, ObjectNode node) {
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(t != null){
			User vo = (User)t;
			
			node.put(KEY_PK, vo.getId());
			node.put(KEY_CODE, vo.getId());
			if(!this.isBlank(vo.getFirstName()) && !this.isBlank(vo.getLastName())){
				node.put(KEY_NAME, vo.getFirstName() + " " + vo.getLastName());
			}else if(!this.isBlank(vo.getFirstName())){
				node.put(KEY_NAME, vo.getFirstName());
			}else if(!this.isBlank(vo.getLastName())){
				node.put(KEY_NAME, vo.getLastName());
			}else{
				node.put(KEY_NAME, "");
			}
			
			node.put(KEY_ID, vo.getId());
			node.put(KEY_PID, "");
			
			node.put(KEY_NODE_TYPE, User.class.getSimpleName());
		}
		
		return node;
	}
	
}
