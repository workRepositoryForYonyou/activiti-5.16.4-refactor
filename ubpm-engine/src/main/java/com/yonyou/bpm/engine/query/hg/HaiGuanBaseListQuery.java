/**
 * 
 */
package com.yonyou.bpm.engine.query.hg;

import com.yonyou.bpm.engine.query.BaseListQuery;

/**
 * @author Administrator
 *
 */
public abstract class HaiGuanBaseListQuery extends BaseListQuery {

	public static final String KEY_VIEW_VALUE = "viewValue";
	
	public static final String KEY_APP_VALUE = "appValue";
	
	public static final String KEY_APP_CODE = "appCode";
	
	public static final String KEY_VIEW_APP_VALUE = "viewAppValue";
	
	public static final String KEY_PARENT_ORG_VALUE = "rootOrgValue";
	
	public static final String KEY_ORG_VALUE = "orgValue";
	
	public static final String KEY_ROLE_VALUE = "roleValue";
	
	public static final String KEY_USER_GROUP_VALUE = "userGroupValue";
	
	public static final String KEY_USER_VALUE = "userValue";
	
	public static final String KEY_EXT_ATTRS = "extAttrs";
	
	public static final String KEY_VIEW_CATEGORY = "viewCategory";
	
	public static final String KEY_APP_CATEGORY = "appCategory";
	
	public static final String KEY_PARENT_ORG_CATEGORY = "rootOrgCategory";
	
	public static final String KEY_ORG_CATEGORY = "orgCategory";
	
	public static final String KEY_ROLE_CATEGORY = "roleCategory";
	
	public static final String KEY_ROLE_CATEGORIES = "roleCategories";
	
	public static final String KEY_GROUP_CATEGORY = "groupCategory";
	
	public static final String KEY_USER_CATEGORY = "userCategory";
	
	public static final String KEY_DELEGATION_CATEGORIES = "delegationCategories";
	
	public static final String KEY_OBJECT_STATUS_CATEGORY = "objectStatusCategory";
	
	public static final String KEY_LIST_OBJECT_CATEGORY = "listObjectCategory";
	
	public static final String KEY_ORG_RANK = "orgRank";
	
	public static final String KEY_ORG_TYPE = "orgType";
	
	public static final String KEY_ORG_CLASS = "orgClass";
	
	public static final String KEY_USER_RANK = "userRank";
	
	public static final String KEY_DEPTH = "depth";
	
	public static final String KEY_QUERY_TYPE = "queryType";
	
	public static final String KEY_QUERY_PART = "queryPart";
	
	public static final String ORG_NODE_PROPERTY_START = "OrgNodeProperty_";
	
	public static final String GROUP_NODE_PROPERTY_START = "GroupNodeProperty_";
	
	public static final String USER_NODE_PROPERTY_START = "UserNodeProperty_";
	
	public static final String SIDELINE_NODE_PROPERTY_START = "SidelineNodeProperty_";
	
	public static final String QUERY_ROOT = "root";
	
	public static final String QUERY_CHILDREN = "children";
	
	public static final String GET_ROLES = "getRoles";
	
	public static final String GET_ROLES_IN_USER = "getRolesInUser";
	
	public static final String GET_USER_IN_ORG = "getUserInOrg";
	
	public static final String GET_USER_IN_ROLE = "getUserInRole";
	
	public static final String GET_USER_IN_GROUP = "getUserInGroup";
	
	public static final String GET_USER_IDENTITY_INFOS = "getUserIdentityInfos";
	
	public static final String VIEW_APPLICATION_NODE_PROPERTY_START = "ViewApplication_";
	
}
