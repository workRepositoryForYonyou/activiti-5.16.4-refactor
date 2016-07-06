/**
 * 
 */
package com.yonyou.bpm.engine.query;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author chouhl
 *
 */
public interface IListQuery {
	
	public static final String KEY_TOTAL_COUNT = "totalCount";
	
	public static final String KEY_CONTENT = "content";
	
	public ObjectNode loadDatas(int start, int count, String searchText, Map<String, Object> queryConditions);
	
	public ObjectNode loadDatas(String searchText, Map<String, Object> queryConditions);
	
	public ObjectNode loadData(String pk, Map<String, Object> queryConditions);
}