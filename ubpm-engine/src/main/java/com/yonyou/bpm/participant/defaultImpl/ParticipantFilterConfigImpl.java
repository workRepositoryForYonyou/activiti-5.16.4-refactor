package com.yonyou.bpm.participant.defaultImpl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.yonyou.bpm.participant.ParticipantType;
import com.yonyou.bpm.participant.adapter.ParticipantFilterAdapter;
import com.yonyou.bpm.participant.config.ParticipantConfigType;
import com.yonyou.bpm.participant.config.ParticipantFilterConfig;
import com.yonyou.bpm.utils.ClassUtils;

public class ParticipantFilterConfigImpl implements ParticipantFilterConfig,Serializable{

	
	private static final long serialVersionUID = 1L;
	private String id;
	private String code=ParticipantType.USER.toString();
	private String name;
	private String adapterClazz;
	private int priority;
	private boolean enable;
	public ParticipantFilterConfigImpl(){
		
	}
	public ParticipantFilterConfigImpl(String code,String name){
		this.code=code;
		this.name=name;
	}
	@Override
	public ParticipantFilterAdapter getAdapter() {
		if(!this.isEnable())return null;
		if(StringUtils.isBlank(this.getAdapterClazz())){
			this.enable=false;
			return null;
		}
		Object obj=ClassUtils.newInstance(this.getAdapterClazz());
		if(obj==null){
			this.enable=false;
			return null;
		}
		if (!(obj instanceof ParticipantFilterAdapter)) {
			this.enable=false;
			return null;
		}
		return (ParticipantFilterAdapter)obj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdapterClazz() {
		return adapterClazz;
	}

	public void setAdapterClazz(String adapterClazz) {
		this.adapterClazz = adapterClazz;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}


}
