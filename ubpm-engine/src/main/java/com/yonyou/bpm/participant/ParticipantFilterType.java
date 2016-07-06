package com.yonyou.bpm.participant;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * BPM平台默认提供限定类型
 * @author zhaohb
 *
 */
public enum ParticipantFilterType {
	/**同集团*/
	SAME_COROP,
	/**同组织*/
	SAME_ORG,
	/**同部门*/
	SAME_DEPT,
	/**同用户组*/
	SAME_USER_GROUP;
	
	public static ParticipantFilterType[] getTypesFromString(String string) {
	    List<ParticipantFilterType> result = new ArrayList<ParticipantFilterType>();
	    if(string != null && !string.isEmpty()) {
	      String[] split = StringUtils.split(string, ",");
	      for(String typeName : split) {
	        boolean found = false;
	        for(ParticipantFilterType type : values()) {
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
	    
	    return result.toArray(new ParticipantFilterType[0]);
	  }
	

}
