package com.yonyou.bpm.engine.configurator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.parse.BpmnParseHandler;
import org.springframework.stereotype.Component;

import com.yonyou.bpm.engine.behavior.BpmBehaviorFactory;
import com.yonyou.bpm.engine.commandinvoker.BpmCommandInvoker;
import com.yonyou.bpm.engine.conf.BpmEngineConfiguration;
import com.yonyou.bpm.engine.db.BpmDbSqlSessionFactory;
import com.yonyou.bpm.engine.parser.BpmSequenceFlowParserHandler;
import com.yonyou.bpm.engine.query.sysdefault.SysDefModelListQuery;
import com.yonyou.bpm.engine.query.sysdefault.SysDefProcDefListQuery;
import com.yonyou.bpm.engine.query.sysdefault.SysDefUserGroupListQuery;
import com.yonyou.bpm.engine.query.sysdefault.SysDefUserListQuery;
import com.yonyou.bpm.engine.query.util.QueryKey;
/**
 * 配置器统一配置，
 * @author zhaohb
 *
 */
@Component("BpmCommandInterceptorConfigurator_UAP_")
public class BpmCommandInterceptorConfigurator implements ProcessEngineConfigurator{

	@Override
	public void beforeInit(
			ProcessEngineConfigurationImpl processEngineConfiguration) {
		//加载静态代码块
		new BpmDbSqlSessionFactory();
		//命令执行器
		processEngineConfiguration.setCommandInvoker(new BpmCommandInvoker());
		//扩展解析器
		List<BpmnParseHandler>  customDefaultBpmnParseHandlers=processEngineConfiguration.getCustomDefaultBpmnParseHandlers();
		//不允许平台外的替换默认类型的ParseHandler
		if(customDefaultBpmnParseHandlers==null){
			customDefaultBpmnParseHandlers=new LinkedList<BpmnParseHandler>();
		}
		customDefaultBpmnParseHandlers.add(new BpmSequenceFlowParserHandler());
		
		
	}

	@Override
	public void configure(ProcessEngineConfigurationImpl processEngineConfiguration) {
		//绑定服务
//		this.bindBpmServices(processEngineConfiguration);
		this.bindBpmRefServices(processEngineConfiguration);
		
		//动作工厂
		BpmBehaviorFactory activityBehaviorFactory=new BpmBehaviorFactory();
		activityBehaviorFactory.setExpressionManager(processEngineConfiguration.getExpressionManager());
		processEngineConfiguration.setActivityBehaviorFactory(activityBehaviorFactory);
		
		//设置系统默认系统管理员角色
		this.setSystemRoles(processEngineConfiguration);
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
//	private  void bindBpmServices(ProcessEngineConfigurationImpl processEngineConfiguration){
//		BpmEngineConfiguration bpmEngineConfiguration=null;
//		
//		SqlSessionFactory  sqlSessionFactory =processEngineConfiguration.getSqlSessionFactory();
//		if(processEngineConfiguration instanceof BpmEngineConfiguration){
//			bpmEngineConfiguration=	(BpmEngineConfiguration)processEngineConfiguration;
//			if(sqlSessionFactory!=null){
//				CategoryService categoryService=new CategoryServiceImpl(sqlSessionFactory);
//				bpmEngineConfiguration.setCategoryService(categoryService);
//			}
//		}
//		
//	}
	
	private  void bindBpmRefServices(ProcessEngineConfigurationImpl processEngineConfiguration){
		if(processEngineConfiguration instanceof BpmEngineConfiguration){
			BpmEngineConfiguration bpmEngineConfiguration =	(BpmEngineConfiguration)processEngineConfiguration;
			bpmEngineConfiguration.getListQueries().put(QueryKey.modelQuery.name(), new SysDefModelListQuery(bpmEngineConfiguration.getRepositoryService()));
			bpmEngineConfiguration.getListQueries().put(QueryKey.procDefQuery.name(), new SysDefProcDefListQuery(bpmEngineConfiguration.getRepositoryService()));
			bpmEngineConfiguration.getListQueries().put(QueryKey.userQuery.name(), new SysDefUserListQuery(bpmEngineConfiguration.getIdentityService()));
			bpmEngineConfiguration.getListQueries().put(QueryKey.userGroupQuery.name(), new SysDefUserGroupListQuery(bpmEngineConfiguration.getIdentityService()));
		}
		
	}

	/**
	 * 设置系统管理员角色
	 * @param processEngineConfiguration
	 */
	private void setSystemRoles(ProcessEngineConfigurationImpl processEngineConfiguration){
		if(processEngineConfiguration instanceof BpmEngineConfiguration){
			BpmEngineConfiguration bpmEngineConfiguration = (BpmEngineConfiguration)processEngineConfiguration;
			bpmEngineConfiguration.setChiefAdminRoleCodes(Arrays.asList(new String[]{"superadmin"}));
		}
	}
	
}
