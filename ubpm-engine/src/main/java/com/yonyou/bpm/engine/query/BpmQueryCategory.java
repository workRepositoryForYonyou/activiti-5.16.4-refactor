/**
 * 
 */
package com.yonyou.bpm.engine.query;

/**
 * @author chouhl
 *
 * 2015年5月20日 上午10:41:08
 */
public class BpmQueryCategory {

	public enum HGUserCategory{
		None,//空查询  
		UserGuid,//根据数据USER_GUID查询  
		UserOriginalSort,//根据数据ORIGINAL_SORT查询
		UserAllPathName,//根据数据ALL_PATH_NAME查询
		UserGlobalSort,//根据数据GLOBAL_SORT查询
		UserIdentity//根据数据LogonIdentity查询
	}
	
	public enum HGOrganizationCategory{
		None,//空查询
		OrgGuid,//根据数据ORG_GUID查询
		OrgOriginalSort,//根据数据ORIGINAL_SORT查询
		OrgAllPathName,//根据数据ALL_PATH_NAME查询
		OrgGlobalSort,//根据数据GLOBAL_SORT查询
		OrgCustomsCode//根据CUSTOMS_CODE查询
	}
	
	public enum HGAccreditCategory{
		None,//空
		Guid,//GUID
		Code,//英文标识
		Level//应用层级（预留） 
	}
	
	public enum HGRoleCategory{
		None,//非法  
		System,//系统管理员角色类  
		Application,//应用管理员角色类  
		All//全部功能  
	}
	
	public enum HGDelegationCategory{
		None,//空  
		Original,//原始权限  
		Delegated,//被委派权限  
		All//全部  
	}
	
	public enum HGListObjectCategory{
		None,//非法筛选器  
		Organizations,// 筛选其中要求查询“机构对象”  
		Users,// 筛选其中要求查询“人员对象”  
		Groups,// 筛选器中要求查询“人员组对象”  
		Sideline,// 筛选器中要求查询“人员兼职对象”  
		All// 筛选器中所能允许的所有数据对象  
	}
	
	public enum HGObjectStatusCategory{
		None,//非法筛选  
		Common,//筛选器中要求查询正常使用的数据对象  
		DirectLogic,// 筛选器中要求查询“直接逻辑删除对象”  
		ConjunctOrgLogic,// 筛选器中要求查询“因部门导致数据逻辑删除对象”  
		ConjunctUserLogic,// 筛选器中要求查询“因人员导致数据逻辑删除对象”  
		All// 系统中所有的数据对象  
	}
	
}
