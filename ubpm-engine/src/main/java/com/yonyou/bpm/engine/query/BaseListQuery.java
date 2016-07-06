/**
 * 
 */
package com.yonyou.bpm.engine.query;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author chouhl
 *
 */
public abstract class BaseListQuery implements IListQuery{
	
	public static final String KEY_PK = "pk";
	public static final String KEY_CODE = "code";
	public static final String KEY_NAME = "name";
	public static final String KEY_ID = "id";
	public static final String KEY_PID = "pId";
	public static final String KEY_CHILDREN = "children";
	public static final String KEY_NODE_TYPE = "nodeType";
	public static final String KEY_TYPE = "type";
	
	public static final String NODE_PROPERTY_START = "NodeProperty_";
	public static final String NODE_PROPERTY_IS_PARENT = "isParent";
	public static final String NODE_PROPERTY_CAN_SELECT = "canselect";
	public static final String NODE_PROPERTY_NO_CHECK = "nocheck";
	
	protected Logger logger = Logger.getLogger(getClass());
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public void logError(Object message, Throwable t){
		this.logger.error(message, t);
	}
	
	public static ObjectMapper getMapper(){
		return mapper;
	}
	
	@Override
	public ObjectNode loadDatas(String searchText, Map<String, Object> queryConditions) {
		return this.loadDatas(0, 0, searchText, queryConditions);
	}
	
	protected <T extends Object>ObjectNode changeObjectToObjectNode(T t, ObjectNode node, Map<String, Object> params){
		node = this.getDatas(t, node);
		
		node = this.getNeedDatas(t, node);
		
		node = this.addPropertiesToNode(node, params);
		
		return node;
	}
	
	protected <T extends Object>ObjectNode getDatas(T t, ObjectNode node){
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(t != null){
			Class<?> clazz = t.getClass();
			Method[] ms = clazz.getMethods();
			int len = ms != null ? ms.length : 0;
			for(int i = 0; i < len; i++){
				if(ms[i] == null){
					continue;
				}
				if(ms[i].getModifiers() != Modifier.PUBLIC){
					continue;
				}
				if(!ms[i].getName().startsWith("get")){
					continue;
				}
				if(ms[i].getParameterTypes() != null && ms[i].getParameterTypes().length != 0){
					continue;
				}
				
				if(ms[i].getReturnType().isAssignableFrom(Object.class)){
					continue;
				}
				
				if(!ms[i].getReturnType().isAssignableFrom(String.class) && !ms[i].getReturnType().isPrimitive()){
					continue;
				}
				
				Object value = null;
				try {
					value = ms[i].invoke(t, new Object[0]);
				} catch (Exception e) {
					logger.error(e);
				}
				if(value == null){
					continue;
				}
				
				String fieldName = ms[i].getName().replaceFirst("get", "").toLowerCase();
				
				Class<?> rt = ms[i].getReturnType();
				
				if(rt.isAssignableFrom(String.class)){
					node.put(fieldName, (String)value);
				}else if(rt.isAssignableFrom(Integer.TYPE)){
					node.put(fieldName, (Integer)value);
				}else if(rt.isAssignableFrom(Boolean.TYPE)){
					node.put(fieldName, (Boolean)value);
				}
			}
		}
		
		return node;
	}
	
	/**
	 * 从T中获取必须的数据
	 * pk,code,name,id,pid,viewValue,nodeType
	 * @param t
	 * @param node
	 * @return
	 */
	protected abstract <T extends Object>ObjectNode getNeedDatas(T t, ObjectNode node);
	
	protected ObjectNode addPropertiesToNode(ObjectNode node, Map<String, Object> params){
		if(node == null){
			node = getMapper().createObjectNode();
		}
		
		if(params != null && params.size() > 0){
			Iterator<String> keys = params.keySet().iterator();
			
			while(keys.hasNext()){
				String key = keys.next();
				if(!this.isBlank(key) && key.startsWith(NODE_PROPERTY_START)){
					//添加到Node中的属性
					node.put(key.replace(NODE_PROPERTY_START, ""), getMapper().valueToTree(params.get(key)));
				}
			}
		}
		
		return node;
	}
	
	protected <T extends Object>ObjectNode changeListToObjectNode(List<T> list, ObjectNode result, Map<String, Object> params){
		if(result == null){
			result = getMapper().createObjectNode();
		}
		
		ArrayNode array = getMapper().createArrayNode();
		
		int size = list != null ? list.size() : 0;
		if(size > 0){
			for(int i = 0; i < size; i++){
				ObjectNode node = getMapper().createObjectNode();
				node = this.changeObjectToObjectNode(list.get(i), node, params);
				if(result.get(KEY_TOTAL_COUNT) != null){
					node.put(KEY_TOTAL_COUNT, result.get(KEY_TOTAL_COUNT));
				}
				array.add(node);
			}
		}
		
		result.put(KEY_CONTENT, array);
		
		return result;
	}
	
	protected boolean isBlank(String str){
		return str == null || str.equals("");
	}
	
	protected Object getQueryCondition(Map<String, Object> qc, String key){
		if(qc != null && qc.containsKey(key)){
			return qc.get(key);
		}
		
		return null;
	}
	
	@Override
	public Object clone(){
		try {
			return this.getClass().newInstance();
		} catch (Exception e) {
			logger.error(e);
		}
		return this;
	}
	
}