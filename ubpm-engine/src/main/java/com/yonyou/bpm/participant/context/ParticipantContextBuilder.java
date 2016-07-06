package com.yonyou.bpm.participant.context;

import java.util.HashMap;
import java.util.Map;

import com.yonyou.bpm.participant.ProcessParticipantItem;
/**
 * 上下文构造器
 * @author zhaohb
 *
 */
public class ParticipantContextBuilder extends ParticipantContext{
	
	public ParticipantContextBuilder bindAllProcessParticipantItems(Map<String,ProcessParticipantItem> allProcessParticipantItems){
		this.setAllProcessParticipantItems(allProcessParticipantItems);
		return this;
	}
	public ParticipantContextBuilder bindAllProcessParticipantItems(ProcessParticipantItem[] processParticipantItems){
		 if(processParticipantItems==null||processParticipantItems.length==0)return this;
			
		 Map<String,ProcessParticipantItem> allProcessParticipantItems=new HashMap<String, ProcessParticipantItem>(processParticipantItems.length);
		 for (ProcessParticipantItem processParticipantItem : processParticipantItems) {
			 if(processParticipantItem==null)continue;
			 allProcessParticipantItems.put(processParticipantItem.getType(), processParticipantItem);
		 }
		this.setAllProcessParticipantItems(allProcessParticipantItems);
		return this;
	}
	public ParticipantContextBuilder bindOthers( Map<String,Object> others){
		this.setOthers(others);
		return this;
	}
	public ParticipantContextBuilder addObjectToOthers(String key,Object value){
		Map<String,Object> others =this.getOthers();
		if(others==null){
			others=new HashMap<String, Object>(2);
		}
		others.put(key, value);
		return this;
	}
	public ParticipantContext getParticipantContext(){
		return this;
	}
	

}
