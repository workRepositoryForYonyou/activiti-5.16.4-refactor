package com.yonyou.bpm.participant;

import java.io.Serializable;
/**
 * 参与者查找结果{ParticipantType.USER ParticipantType.GROUP}
 * @author zhaohb
 *
 */
public class Participant implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//pk
	private String id;
	//编码
	private String code;
	//名称
	private String name;
	/**默认类型*/
	private String type=ParticipantType.USER.toString();
	/**优先级默认为50*/
	private int  priority=50;
	
	public Participant(){
		
	}
	
	public Participant(String id){
		this.id=id;
	}
	
	public Participant(String id,String code,String name){
		this.id=id;
		this.code=code;
		this.name=name;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public boolean equals(Object obj) {   
        if (obj instanceof Participant) {   
        	Participant participant = (Participant) obj;   
        	if(this.code!=null){
        		 return this.type.equals(participant.type)   
                         && this.id.equals(participant.getId())
                         && this.code.equals(participant.getCode());    
        	}else{
        		 return this.type.equals(participant.type)   
                         && this.id.equals(participant.getId());   
        	}
           
        }   
        return super.equals(obj);  
	}
	@Override
	public int hashCode() {
		return priority*type.hashCode()+ id==null?0: id.hashCode()+code==null?0:code.hashCode();
	}
	@Override
	public String toString() {
		
		return "{id='"+id+"';code='"+code+"';name='"+name+"';type='"+type+"'}";
	}
	


}
