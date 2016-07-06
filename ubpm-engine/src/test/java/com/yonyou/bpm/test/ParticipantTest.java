package com.yonyou.bpm.test;

import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import com.yonyou.bpm.engine.core.ProcessInstanceState;
import com.yonyou.bpm.participant.Participant;
import com.yonyou.bpm.participant.ParticipantService;
import com.yonyou.bpm.participant.ParticipantType;
import com.yonyou.bpm.participant.ProcessParticipant;
import com.yonyou.bpm.participant.ProcessParticipantDetail;
import com.yonyou.bpm.participant.ProcessParticipantItem;
import com.yonyou.bpm.participant.context.ParticipantContext;
import com.yonyou.bpm.participant.context.ParticipantContextBuilder;
import com.yonyou.bpm.participant.defaultImpl.ParticipantServiceImpl;

public class ParticipantTest {
	
	
	ParticipantContext participantContext;
	@Before
	public void init(){
//		ProcessParticipantDetail details=new ProcessParticipantDetail();
//		details.setId("{pk:1,name:2}");
//		details.setCode("admin");
//		details.setName("admin");
//		details.addOthers("otherKey1", "otherValue1");
//		 
//		ProcessParticipantItem processParticipantItem=new ProcessParticipantItem();
//		processParticipantItem.setType(ParticipantType.USER.toString());
//		
//		processParticipantItem.setDetails(new ProcessParticipantDetail[]{details});
//		
//		ProcessParticipant pocessParticipant=new ProcessParticipant();
//		ProcessParticipantItem[] processParticipantItems=new ProcessParticipantItem[]{processParticipantItem};
//		pocessParticipant.setProcessParticipantItems(processParticipantItems);
//		JSONObject jsonArra=JSONObject.fromObject(pocessParticipant);
//		System.out.println(jsonArra);
		String a="{'processParticipantItems':[null,{'others':null,'details':[{'id':'true','others':{'code':'true'},'name':'','code':''}],'type':'defaultuser','filters':[]},null,null],'filters':[]}";
		JSONObject jsonArra=null;
		try{
		 jsonArra=JSONObject.fromObject(a);
		}catch(Exception e){
			e.printStackTrace();
		}
		ProcessParticipant processParticipant2=(ProcessParticipant)JSONObject.toBean(jsonArra, ProcessParticipant.class);
		
		participantContext=new ParticipantContextBuilder()
		.bindAllProcessParticipantItems(processParticipant2.getProcessParticipantItems())
		.getParticipantContext();
		
		
		
	}
	@Test
	public void getParticipants(){
		ParticipantService participantService=new ParticipantServiceImpl();
		List<Participant> list=participantService.getParticipants(participantContext);
		if(list!=null&&list.size()>0){
			for (Participant participant : list) {
				System.out.println(participant.toString());
			}
		}
		
	}
	@Test
	public void getProcessInstanceState(){
		ProcessInstanceState.DELETE.toString();
	}

}
