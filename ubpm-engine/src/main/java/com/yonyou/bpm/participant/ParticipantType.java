package com.yonyou.bpm.participant;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * BPM平台默认提供参与者类型
 * @author zhaohb
 *
 */
public enum ParticipantType {
	/**用户*/
	USER,
	/**用户组*/
	USR_GROUP,
	/**用户角色*/
	USER_ROLE,
	/**集团*/
	CORP,
	/**组织*/
	ORG,
	/**部门*/
	DEPT,
	/**岗位*/
	POST,
	/**职务*/
	JOB,
	/**人员*/
	PSNDOC;
	
	public static ParticipantType[] getTypesFromString(String string) {
	    List<ParticipantType> result = new ArrayList<ParticipantType>();
	    if(string != null && !string.isEmpty()) {
	      String[] split = StringUtils.split(string, ",");
	      for(String typeName : split) {
	        boolean found = false;
	        for(ParticipantType type : values()) {
	          if(typeName.equals(type.name())) {
	            result.add(type);
	            found = true;
	            break;
	          }
	        }
	        if(!found) {
	          throw new IllegalArgumentException("Invalid event-type: " + typeName);
	        }
	      }
	    }
	    
	    return result.toArray(new ParticipantType[0]);
	  }
	
	
}
