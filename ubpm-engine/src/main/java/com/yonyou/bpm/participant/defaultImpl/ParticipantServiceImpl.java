package com.yonyou.bpm.participant.defaultImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ParticipantFactory;
import com.yonyou.bpm.participant.ParticipantService;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.adapter.ParticipantAdapter;
import com.yonyou.bpm.participant.adapter.ParticipantFilterAdapter;
import com.yonyou.bpm.participant.config.ParticipantConfig;
import com.yonyou.bpm.participant.config.ParticipantFilterConfig;
import com.yonyou.bpm.participant.context.ParticipantContext;

public class ParticipantServiceImpl implements ParticipantService{
	private static Logger log = LoggerFactory
			.getLogger(ParticipantServiceImpl.class);
	private ParticipantAdapter userAdapter=new UserAdapter();
	@Override
	public Map<String, ParticipantConfig> getParticipantConfigs() {
		return ParticipantFactory.getInstance().getParticipantConfigs();
	}
	@Override
	public Map<String,	ParticipantFilterConfig> getParticipantFilterConfigs(){
		return ParticipantFactory.getInstance().getParticipantFilterConfigs();
	}
	@Override
	public List<Participant> getParticipants(
			ParticipantContext participantContext) {
		Map<String, ParticipantConfig> participantConfigs=participantContext.getParticipantConfigs();
		if(participantConfigs==null){
			throw new IllegalArgumentException("'participantConfigs' can not be null");
		}
		Map<String, ParticipantFilterConfig> participantFilterConfigs=participantContext.getParticipantFilterConfigs();
		boolean isParticipantFilterConfigsNull=participantFilterConfigs==null||participantFilterConfigs.size()==0;
		Map<String,ProcessParticipantItem>  allProcessParticipantItems= participantContext.getAllProcessParticipantItems();
		if(allProcessParticipantItems==null||allProcessParticipantItems.size()==0){
			throw new IllegalArgumentException("'allProcessParticipantItems' can not be null");
		}
		List<Participant> resultList=new ArrayList<Participant>();
		for (Map.Entry<String,ProcessParticipantItem> processParticipantItemEntry : allProcessParticipantItems.entrySet()) {
			
			ProcessParticipantItem processParticipantItem=processParticipantItemEntry.getValue();
			String type=processParticipantItem.getType();
			ParticipantConfig participantConfig=participantConfigs.get(type);
			if(participantConfig==null){
				throw new BpmException("无法找到类型为'"+type+"'的参与者配置项");
			}
			ParticipantAdapter  participantAdapter =participantConfig.getAdapter();
			if(participantAdapter==null)continue;
			log.info("查找参与者服务，正在使用类型为："+type+"的参与者类型，使用查找实现："+participantAdapter.getClass().getName()+"查找....");
			List<Participant> list=participantAdapter.find(processParticipantItem, participantContext);
			int size=list==null?0:list.size();
			log.info("查找到的记录数："+size);
			if(size==0)continue;
			String[] filters=processParticipantItem.getFilters();
			int filtersLen=filters==null?0:filters.length;
			if(filtersLen>0){
				if(!isParticipantFilterConfigsNull){
					for (String filter : filters) {
						ParticipantFilterConfig participantFilterConfig=participantFilterConfigs.get(filter);
						if(participantFilterConfig!=null){
							ParticipantFilterAdapter participantFilterAdapter =participantFilterConfig.getAdapter();
							if(participantFilterAdapter!=null){
								list=participantFilterAdapter.filter(list, participantContext);
							}
						}
					}
					int filterSize=list==null?0:list.size();
					log.info("查找参与者服务，过滤后的记录数："+filterSize);
				}else{
					log.info("查找参与者服务，本次的过滤器类型在系统中无法找到,导致无法过滤！");
				}
			}
			if(list!=null&&list.size()>0){
				for (Participant participant : list) {
					if(participant==null)continue;
					if(resultList.contains(participant))continue;
					resultList.add(participant);
				}
			}
		}
		Context.getCommandContext().addAttribute("userselected", resultList);
		return resultList;
	}
	//可适当做出优化，对于限定未做考虑
	@Override
	public boolean contains(ParticipantContext participantContext, String id) {
		Map<String, ParticipantConfig> participantConfigs=participantContext.getParticipantConfigs();
		Map<String,ProcessParticipantItem>  allProcessParticipantItems= participantContext.getAllProcessParticipantItems();
		if(allProcessParticipantItems==null||allProcessParticipantItems.size()==0){
			throw new IllegalArgumentException("'allProcessParticipantItems' can not be null");
		}
		for (Map.Entry<String,ProcessParticipantItem> processParticipantItemEntry : allProcessParticipantItems.entrySet()) {
			
			ProcessParticipantItem processParticipantItem=processParticipantItemEntry.getValue();
			
			String type=processParticipantItem.getType();
			ParticipantConfig participantConfig=participantConfigs.get(type);
			ParticipantAdapter  participantAdapter =participantConfig.getAdapter();
			if(participantAdapter==null)continue;
			
			boolean isContain=participantAdapter.contains(processParticipantItem, participantContext, id);
			if(isContain){
				return true;
			}
		}
		return false;
	}
	@Override
	public ParticipantAdapter getUserAdapter() {
		return userAdapter;
	}
	public void setUserAdapter(ParticipantAdapter userAdapter) {
		this.userAdapter = userAdapter;
	}
}
