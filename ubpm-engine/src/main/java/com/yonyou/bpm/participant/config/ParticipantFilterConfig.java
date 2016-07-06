package com.yonyou.bpm.participant.config;

import com.yonyou.bpm.participant.adapter.ParticipantFilterAdapter;


public interface ParticipantFilterConfig {
	/**主键*/
	String getId();
	void setId(String id);
	
	String getCode();
	void setCode(String code);
	
	String getName();
	void setName(String name);
	
	/**查找实现*/
	String getAdapterClazz();
	void setAdapterClazz(String adatperClazz);
	
	int getPriority();
	void setPriority(int priority);
	
	boolean isEnable();
	
	ParticipantFilterAdapter getAdapter();

}
