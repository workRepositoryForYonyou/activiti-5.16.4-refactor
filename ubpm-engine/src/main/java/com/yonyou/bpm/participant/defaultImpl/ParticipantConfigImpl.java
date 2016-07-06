package com.yonyou.bpm.participant.defaultImpl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.yonyou.bpm.participant.ParticipantType;
import com.yonyou.bpm.participant.adapter.ParticipantAdapter;
import com.yonyou.bpm.participant.config.ParticipantConfig;
import com.yonyou.bpm.participant.config.ParticipantConfigType;
import com.yonyou.bpm.utils.ClassUtils;

public class ParticipantConfigImpl implements ParticipantConfig,Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String type=ParticipantConfigType.DEFAULT.toString();
	private String code=ParticipantType.USER.toString();
	private String name;
	private String adapterClazz;
	private String editType;
	private String refUrl;
	private int priority;
	private boolean enable;
	public ParticipantConfigImpl(){
		
	}
	public ParticipantConfigImpl(String code,String name){
		this.code=code;
		this.name=name;
	}
	@Override
	public String getId() {
		return this.id;
	}

	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type=type;
	}
	@Override
	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public void setCode(String code) {
		this.code=code;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	@Override
	public String getRefUrl() {
		return refUrl;
	}
	@Override
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	@Override
	public String getAdapterClazz() {
		return this.adapterClazz;
	}

	@Override
	public void setAdapterClazz(String adapterClazz) {
		this.adapterClazz=adapterClazz;
	}
	public String getEditType() {
		return editType;
	}
	public void setEditType(String editType) {
		this.editType = editType;
	}
	@Override
	public int getPriority() {
		return priority;
	}
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public boolean isEnable() {
		return this.enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public ParticipantAdapter getAdapter() {
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
		if (!(obj instanceof ParticipantAdapter)) {
			this.enable=false;
			return null;
		}
		return (ParticipantAdapter)obj;
	}

}
