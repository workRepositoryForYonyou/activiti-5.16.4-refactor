package com.yonyou.bpm.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.context.ExecutionContext;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.assign.AssignInfo;
import com.yonyou.bpm.engine.cmd.AssignCheckCmd;
import com.yonyou.bpm.participant.ProcessParticipant;
/**
 * 流程平台bean
 * @author zhaohb
 *
 */
public class BpmUtilsBean extends XpathUtilBean{
	 private static Logger logger = LoggerFactory
	            .getLogger(BpmUtilsBean.class);
	/**
	 * 获取用户
	 * @param participantsJsonStr
	 * @return
	 */
	public List<String> getUser(String participantsJsonStr){
		List<String> result=null;
		
		ActivityImpl  activityImpl=null;
		ExecutionEntity executionEntity=null;
		 if(Context.isExecutionContextActive()&&!(Context.getCommandContext().getCommand() instanceof AssignCheckCmd)){
			  executionEntity=Context.getExecutionContext().getExecution();
			  activityImpl= executionEntity.getActivity();
			  //指派以及调整
			  //指派以及调整
				boolean needAssignInfo=Context.getCommandContext().getAttribute(AssignInfo.ASSIGNINFO_NEED)!=null;
				if(activityImpl!=null&&AssignInfoUtils.isAssignAble(activityImpl)||needAssignInfo){
					result=AssignInfoUtils.getRuntimeAssignUser();
					if(needAssignInfo&&(result==null||result.size()==0)){
						 throw new BpmException("指派信息不能为空！");
					}
				}
		 }
		 if(executionEntity!=null){
			 logger.info( "正在运行实例["+executionEntity.getId()+"]>活动[name="
				+ activityImpl.getProperty("name") + "(id="+activityImpl.getId()+")]上查找用户");
		 }
		//未检查指派信息检查！
		if(result==null||result.size()==0){
			if(participantsJsonStr==null||"".equals(participantsJsonStr.trim()))return null;
			JSONObject json = null;
			try{
				json=JSONObject.fromObject(participantsJsonStr);
			}catch(Exception e){
				logger.error("解析参与者json报错了！", e);
				throw new BpmException("解析参与者json报错了！",e);
			}
			 ProcessParticipant processParticipant = (ProcessParticipant)JSONObject.toBean(json, ProcessParticipant.class);
			 //测试将异常吞掉
			 try{
				 result= ParticipantUtils.getUser(processParticipant);
			 }catch(Exception e){
				 logger.error("查询参与者报错了！"+participantsJsonStr, e);
				 throw new BpmException("参与者查询报错了！",e);
			 }

		}else{
			logger.info("正在使用指派信息作为流程参与者");
		}
		
		if((result==null||result.size()==0)&&activityImpl!=null){
			throw new ActivitiObjectNotFoundException( "活动["
					+ activityImpl.getProperty("name") + "("+activityImpl.getId()+")]上找不到用户");
		}
		
		//过滤重复
		List<String> result2= new ArrayList<String>();
		for (String string : result) {
			if(result2.contains(string)||null==string||"".equals(string))continue;
			result2.add(string);
		}
		return result2;
	}
	/**
	 * 将参数转换为参数
	 * @param args
	 * @return
	 */
	public List<String> asList(String... args){
		return Arrays.asList(args);
	}
	public List<String> testList(){
		return Arrays.asList(new String[]{"usera","userb","userc"});
			
	}
	public List<String> testList(int size){
		List<String> list=new ArrayList<String>();
		for(int i=0;i<size;i++){
			list.add("user"+i+"");
		}
		return list;
			
	}
	/**
	 * 将字符串转换为
	 * @param str
	 * @param splitor
	 * @return
	 */
	public List<String> asList(String str,String splitor){
		if(str==null||"".equals(str.trim()))return null;
		String[] strs=str.split(splitor);
		
		List<String> list=new ArrayList<String>(strs.length);
		for (String string : strs) {
			list.add(string);
		}
		return list;
		
	}
	public boolean assignTarget(){
		CommandContext commandContext=Context.getCommandContext();
		if(commandContext==null)return true;
		Command command=commandContext.getCommand();
		if(command instanceof AssignCheckCmd){
			return true;
		}
		ExecutionContext  executionContext =null;
		if(Context.isExecutionContextActive()){
			executionContext=Context.getExecutionContext();
		}
		ExecutionEntity executionEntity=(ExecutionEntity)commandContext.getAttribute("curExecution");
		TransitionImpl transitionImpl=(TransitionImpl)commandContext.getAttribute("curTransition");
		if(executionEntity==null&&executionContext!=null){
			executionEntity=executionContext.getExecution();
		}
		
		return AssignInfoUtils.assignTarget(command, transitionImpl, executionEntity);
	}

}
