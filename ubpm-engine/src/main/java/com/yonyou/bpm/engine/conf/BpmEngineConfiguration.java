package com.yonyou.bpm.engine.conf;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParseHandlers;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultListenerFactory;
import org.activiti.engine.impl.cfg.DefaultBpmnParseFactory;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cfg.SpringBeanFactoryProxyMap;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.persistence.deploy.Deployer;
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.rules.RulesDeployer;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.rest.form.MonthFormType;
import org.activiti.rest.form.ProcessDefinitionFormType;
import org.activiti.rest.form.UserFormType;
import org.activiti.spring.SpringEntityManagerSessionFactory;
import org.activiti.spring.SpringExpressionManager;
import org.activiti.spring.SpringTransactionContextFactory;
import org.activiti.spring.SpringTransactionInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import com.yonyou.bpm.BpmException;
import com.yonyou.bpm.core.impl.AgentServiceImpl;
import com.yonyou.bpm.core.impl.CategoryServiceImpl;
import com.yonyou.bpm.core.user.IUserService;
import com.yonyou.bpm.core.user.impl.UserServiceImpl;
import com.yonyou.bpm.deploy.MD5ResourceMapper;
import com.yonyou.bpm.engine.BpmProcessEngine;
import com.yonyou.bpm.engine.query.IListQuery;
import com.yonyou.bpm.engine.service.BpmFormServiceImpl;
import com.yonyou.bpm.engine.service.BpmHistoryServiceImpl;
import com.yonyou.bpm.engine.service.BpmIdentityServiceImpl;
import com.yonyou.bpm.engine.service.BpmManagementServiceImpl;
import com.yonyou.bpm.engine.service.BpmProcessEngineImpl;
import com.yonyou.bpm.engine.service.BpmRepositoryServiceImpl;
import com.yonyou.bpm.engine.service.BpmRuntimeServiceImpl;
import com.yonyou.bpm.engine.service.BpmTaskServiceImpl;
import com.yonyou.bpm.participant.ParticipantService;
import com.yonyou.bpm.participant.defaultImpl.ParticipantServiceImpl;
import com.yonyou.bpm.trace.LoggingTracer;

/**
 * 实现与 spring 集成，并支持 spring bean annotation 注入的流程引擎配置的实现；
 * 
 * @author haiq
 *
 */
public class BpmEngineConfiguration extends ProcessEngineConfigurationImpl
		implements ApplicationContextAware {

	public static final String PROP_ENGINE_SCHEMA_UPDATE = "engine.schema.update";
	public static final String PROP_ENGINE_ACTIVATE_JOBEXECUTOR = "engine.activate.jobexecutor";
	public static final String PROP_ENGINE_HISTORY_LEVEL = "engine.history.level";

	public static final String PROP_MAIL_SERVER_HOST = "mail.server.host";
	public static final String PROP_MAIL_SERVER_PORT = "mail.server.port";
	public static final String PROP_MAIL_SERVER_USERNAME = "mail.server.username";
	public static final String PROP_MAIL_SERVER_PASSWORD = "mail.server.password";
	public static final String PROP_MAIL_SERVER_SSL = "mail.server.ssl";
	public static final String PROP_MAIL_SERVER_TLS = "mail.server.tls";
	public static final String PROP_MAIL_DEFAULT_FROM = "mail.default.from";

	public static final String PROP_DIAGRAM_FONT = "diagram.font";
	public static final String PROP_DIAGRAM_FONT_LABEL = "diagram.font.label";

	private static final String[] DATABASE_TYPES = { DATABASE_TYPE_DB2,
			DATABASE_TYPE_H2, DATABASE_TYPE_MSSQL, DATABASE_TYPE_MYSQL,
			DATABASE_TYPE_ORACLE, DATABASE_TYPE_POSTGRES };

	@Autowired
	private Environment environment;

	@Autowired(required = false)
	private Resource customLibDirectory;

	// @Autowired(required = false)
	// private UserAuthenticator authenticator;

	protected PlatformTransactionManager transactionManager;
	protected ApplicationContext applicationContext;
	protected Integer transactionSynchronizationAdapterOrder = null;

	// SERVICES
	protected CategoryServiceImpl categoryService;
	protected AgentServiceImpl agentService;
	protected ParticipantService participantService = new ParticipantServiceImpl();
	
	
	private UserServiceImpl userService;
	protected Map<String, IListQuery> listQueries = new HashMap<String, IListQuery>();
	//配置的海关总管理员角色CODE
	protected List<String> chiefAdminRoleCodes = new ArrayList<String>(0);
	//配置的海关总管理员视角和应用CODE
	protected Map<String, String[]> chiefAdminViewAndAppCodes = new HashMap<String, String[]>(0);
	//配置的海关普通管理员视角
	protected String[] generalAdminViewCodes = new String[0];
	
	public BpmEngineConfiguration() {
		setDatabaseSchemaUpdate("false");
		// setJobExecutorActivate(true);
		setTransactionsExternallyManaged(true);
	}

	@Override
	protected void init() {
		// bug fixed: Init beans before ExpressionManager initialization;
		initBeans();

		extInitialization();

		super.init();

	}
	
	@Override
	protected void initServices() {
		super.initServices();
	}
	
	@Override
	protected void initSqlSessionFactory() {
		super.initSqlSessionFactory();
		
		initServicesWithSqlSessionFactory();
	}
	
	protected void initServicesWithSqlSessionFactory(){
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		categoryService = new CategoryServiceImpl(sqlSessionFactory);
		categoryService.setCommandExecutor(commandExecutor);
		
		agentService= new AgentServiceImpl(sqlSessionFactory);
		agentService.setCommandExecutor(commandExecutor);
		
		userService = new UserServiceImpl(sqlSessionFactory);
		userService.setCommandExecutor(commandExecutor);
	}
	

	@Override
	protected void initBeans() {
		if (getBeans() == null) {
			// 默认将应用程序上下文所有的 bean 都允许表达式管理器访问；
			setBeans(new SpringBeanFactoryProxyMap(applicationContext));
		}
	}

	@Override
	protected void initExpressionManager() {
		if (getExpressionManager() == null && applicationContext != null) {
			setExpressionManager(new SpringExpressionManager(
					applicationContext, getBeans()));
		}
		if (getExpressionManager() == null) {
			setExpressionManager(new ExpressionManager(beans));
		}
	}
	 protected Collection< ? extends Deployer> getDefaultDeployers() {
		    List<Deployer> defaultDeployers = new ArrayList<Deployer>();

		    if (bpmnDeployer == null) {
		      bpmnDeployer = new BpmnDeployer();
		    }
		      
		    bpmnDeployer.setExpressionManager(expressionManager);
		    bpmnDeployer.setIdGenerator(idGenerator);
		    
		    if (bpmnParseFactory == null) {
		      bpmnParseFactory = new DefaultBpmnParseFactory();
		    }
		    
		    if (activityBehaviorFactory == null) {
		      DefaultActivityBehaviorFactory defaultActivityBehaviorFactory = new DefaultActivityBehaviorFactory();
		      defaultActivityBehaviorFactory.setExpressionManager(expressionManager);
		      activityBehaviorFactory = defaultActivityBehaviorFactory;
		    }
		    
		    if (listenerFactory == null) {
		      DefaultListenerFactory defaultListenerFactory = new DefaultListenerFactory();
		      defaultListenerFactory.setExpressionManager(expressionManager);
		      listenerFactory = defaultListenerFactory;
		    }
		    
		    if (bpmnParser == null) {
		      bpmnParser = new BpmnParser();
		    }
		    
		    bpmnParser.setExpressionManager(expressionManager);
		    bpmnParser.setBpmnParseFactory(bpmnParseFactory);
		    bpmnParser.setActivityBehaviorFactory(activityBehaviorFactory);
		    bpmnParser.setListenerFactory(listenerFactory);
		    
		    List<BpmnParseHandler> parseHandlers = new ArrayList<BpmnParseHandler>();
		    if(getPreBpmnParseHandlers() != null) {
		      parseHandlers.addAll(getPreBpmnParseHandlers());
		    }
		    parseHandlers.addAll(getDefaultBpmnParseHandlers());
		    if(getPostBpmnParseHandlers() != null) {
		      parseHandlers.addAll(getPostBpmnParseHandlers());
		    }
		    
		    BpmnParseHandlers bpmnParseHandlers = new BpmnParseHandlers();
		    bpmnParseHandlers.addHandlers(parseHandlers);
		    bpmnParser.setBpmnParserHandlers(bpmnParseHandlers);
		    
		    bpmnDeployer.setBpmnParser(bpmnParser);
		    
		    defaultDeployers.add(bpmnDeployer);
		    RulesDeployer rulesDeployer=new RulesDeployer();
		    defaultDeployers.add(rulesDeployer);
		    return defaultDeployers;
		  }
	private void extInitialization() {
		// 禁止 ImageIO 使用磁盘缓存，避免在流程图生成过程通过创建临时磁盘文件进行图片转换；
		ImageIO.setUseCache(false);

		List<AbstractFormType> formTypes = new ArrayList<AbstractFormType>();
		formTypes.add(new UserFormType());
		formTypes.add(new ProcessDefinitionFormType());
		formTypes.add(new MonthFormType());
		setCustomFormTypes(formTypes);

		ClassLoader serverClassLoader = createServerClassLoader(Thread
				.currentThread().getContextClassLoader());
		setClassLoader(serverClassLoader);

		// regist custom mybatis mapper;
		setCustomMybatisMappers(getCustomMyBatisMapper());

		// inject extend service;
		setRuntimeService(new BpmRuntimeServiceImpl());
		setRepositoryService(new BpmRepositoryServiceImpl());
		setTaskService(new BpmTaskServiceImpl(this));
		setIdentityService(new BpmIdentityServiceImpl());
		setFormService(new BpmFormServiceImpl());
		setHistoryService(new BpmHistoryServiceImpl(this));
		setManagementService(new BpmManagementServiceImpl());
	}

	/**
	 * @param parentClassLoader
	 * @return
	 */
	private ClassLoader createServerClassLoader(ClassLoader parentClassLoader) {
		if (customLibDirectory == null) {
			return parentClassLoader;
		}
		try {
			File javaCodeDplDir = customLibDirectory.getFile();
			if (!javaCodeDplDir.isDirectory()) {
				return parentClassLoader;
			}
			File[] jarFiles = javaCodeDplDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jar");
				}
			});
			URL[] jarUrls = new URL[jarFiles == null ? 0 : jarFiles.length];
			for (int i = 0; i < jarUrls.length; i++) {
				jarUrls[i] = jarFiles[i].toURI().toURL();
			}
			ClassLoader customerLoader = parentClassLoader;
			if (jarUrls.length > 0) {
				customerLoader = new URLClassLoader(jarUrls, parentClassLoader);
			}
			return customerLoader;
		} catch (MalformedURLException e) {
			throw new BpmException(e);
		} catch (IOException e) {
			throw new BpmException(e);
		}
	}

	private Set<Class<?>> getCustomMyBatisMapper() {
		Set<Class<?>> mappers = new HashSet<Class<?>>();
		mappers.add(MD5ResourceMapper.class);

		return mappers;
	}

	@Override
	public BpmProcessEngine buildProcessEngine() {
		//ProcessEngine processEngine = super.buildProcessEngine();
		init();
		BpmProcessEngineImpl processEngine = new BpmProcessEngineImpl(this);
		ProcessEngines.setInitialized(true);
		return processEngine;
	}

	public void setTransactionSynchronizationAdapterOrder(
			Integer transactionSynchronizationAdapterOrder) {
		this.transactionSynchronizationAdapterOrder = transactionSynchronizationAdapterOrder;
	}

	@Override
	protected void initDefaultCommandConfig() {
		if (defaultCommandConfig == null) {
			defaultCommandConfig = new CommandConfig()
					.setContextReusePossible(true);
		}
	}

	@Override
	protected CommandInterceptor createTransactionInterceptor() {
		if (transactionManager == null) {
			throw new ActivitiException(
					"transactionManager is required property for SpringProcessEngineConfiguration, use "
							+ StandaloneProcessEngineConfiguration.class
									.getName() + " otherwise");
		}

		return new SpringTransactionInterceptor(transactionManager);
	}

	@Override
	protected void initTransactionContextFactory() {
		if (transactionContextFactory == null && transactionManager != null) {
			transactionContextFactory = new SpringTransactionContextFactory(
					transactionManager, transactionSynchronizationAdapterOrder);
		}
	}

	@Override
	protected void initJpa() {
		super.initJpa();
		if (jpaEntityManagerFactory != null) {
			sessionFactories.put(EntityManagerSession.class,
					new SpringEntityManagerSessionFactory(
							jpaEntityManagerFactory, jpaHandleTransaction,
							jpaCloseEntityManager));
		}
	}

	@Override
	public ProcessEngineConfiguration setDatabaseType(String databaseType) {
		// 如果不是预定义的数据库类型（比如 jndi），则设置为 null；
		if (databaseType != null) {
			boolean isPredefinedType = false;
			for (String predefinedDBtype : DATABASE_TYPES) {
				if (databaseType.equalsIgnoreCase(predefinedDBtype)) {
					databaseType = predefinedDBtype;
					isPredefinedType = true;
					break;
				}
			}
			if (!isPredefinedType) {
				databaseType = null;
			}
		}
		return super.setDatabaseType(databaseType);
	}

	@Autowired
	@Qualifier("dataSource")
	@Override
	public ProcessEngineConfiguration setDataSource(DataSource dataSource) {
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
			return super.setDataSource(dataSource);
		} else {
			// Wrap datasource in Transaction-aware proxy
			DataSource proxiedDataSource = new TransactionAwareDataSourceProxy(
					dataSource);
			return super.setDataSource(proxiedDataSource);
		}
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	@Autowired()
	@Qualifier("transactionManager")
	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Autowired(required = false)
	@Qualifier("idGenerator")
	@Override
	public ProcessEngineConfigurationImpl setIdGenerator(IdGenerator idGenerator) {
		return super.setIdGenerator(idGenerator);
	}

	@Autowired(required = false)
	@Qualifier("expressionManager")
	@Override
	public ProcessEngineConfigurationImpl setExpressionManager(
			ExpressionManager expressionManager) {
		return super.setExpressionManager(expressionManager);
	}

	@Autowired(required = false)
	@Qualifier("processDiagramGenerator")
	@Override
	public ProcessEngineConfiguration setProcessDiagramGenerator(
			ProcessDiagramGenerator processDiagramGenerator) {
		return super.setProcessDiagramGenerator(processDiagramGenerator);
	}

	@Autowired(required = false)
	@Qualifier("processDefinitionCache")
	@Override
	public ProcessEngineConfigurationImpl setProcessDefinitionCache(
			DeploymentCache<ProcessDefinitionEntity> processDefinitionCache) {
		return super.setProcessDefinitionCache(processDefinitionCache);
	}

	@Autowired(required = false)
	@Qualifier("knowledgeBaseCache")
	@Override
	public ProcessEngineConfigurationImpl setKnowledgeBaseCache(
			DeploymentCache<Object> knowledgeBaseCache) {
		return super.setKnowledgeBaseCache(knowledgeBaseCache);
	}

	@Autowired(required = false)
	@Qualifier("jobExecutor")
	@Override
	public ProcessEngineConfiguration setJobExecutor(JobExecutor jobExecutor) {
		return super.setJobExecutor(jobExecutor);
	}

	@Override
	public List<ProcessEngineConfigurator> getConfigurators() {
		if (configurators == null) {
			configurators = new LinkedList<ProcessEngineConfigurator>();
		}
		return configurators;
	}

	@Autowired(required = false)
	@Override
	public ProcessEngineConfigurationImpl setConfigurators(
			List<ProcessEngineConfigurator> configurators) {
		getConfigurators().addAll(configurators);
		return this;
	}

	@Override
	public List<BpmnParseHandler> getPreBpmnParseHandlers() {
		if (preBpmnParseHandlers == null) {
			preBpmnParseHandlers = new LinkedList<BpmnParseHandler>();
		}
		return preBpmnParseHandlers;
	}

	@Override
	public ProcessEngineConfigurationImpl setPreBpmnParseHandlers(
			List<BpmnParseHandler> preBpmnParseHandlers) {
		getPreBpmnParseHandlers().addAll(preBpmnParseHandlers);
		return this;
	}

	@Override
	public List<BpmnParseHandler> getPostBpmnParseHandlers() {
		if (postBpmnParseHandlers == null) {
			postBpmnParseHandlers = new LinkedList<BpmnParseHandler>();
		}
		return postBpmnParseHandlers;
	}

	@Override
	public ProcessEngineConfigurationImpl setPostBpmnParseHandlers(
			List<BpmnParseHandler> postBpmnParseHandlers) {
		getPostBpmnParseHandlers().addAll(postBpmnParseHandlers);
		return this;
	}

	@Override
	public List<BpmnParseHandler> getCustomDefaultBpmnParseHandlers() {
		if (customDefaultBpmnParseHandlers == null) {
			customDefaultBpmnParseHandlers = new LinkedList<BpmnParseHandler>();
		}
		return customDefaultBpmnParseHandlers;
	}

	@Override
	public ProcessEngineConfigurationImpl setCustomDefaultBpmnParseHandlers(
			List<BpmnParseHandler> customDefaultBpmnParseHandlers) {
		getCustomDefaultBpmnParseHandlers().addAll(
				customDefaultBpmnParseHandlers);
		return this;
	}

	public boolean isLoggingTraceEnable() {
		return LoggingTracer.isEnable();
	}

	public void setLoggingTraceEnable(boolean loggingTraceEnable) {
		// 开启日志跟踪 ；
		LoggingTracer.setEnable(loggingTraceEnable);
	}

	public ProcessEngineConfigurationImpl setActivityBehaviorFactory(
			ActivityBehaviorFactory activityBehaviorFactory) {
		this.activityBehaviorFactory = activityBehaviorFactory;
		if (this.bpmnParser != null) {
			this.bpmnParser.setActivityBehaviorFactory(activityBehaviorFactory);
		}
		return this;
	}

	public CategoryServiceImpl getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryServiceImpl categoryService) {
		this.categoryService = categoryService;
	}

	public ParticipantService getParticipantService() {
		return participantService;
	}

	public void setParticipantService(ParticipantService participantService) {
		this.participantService = participantService;
	}

	public AgentServiceImpl getAgentService() {
		return agentService;
	}

	public void setAgentService(AgentServiceImpl agentService) {
		this.agentService = agentService;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(UserServiceImpl userService) {
		this.userService = userService;
	}

	public Map<String, IListQuery> getListQueries() {
		return listQueries;
	}

	public void setListQueries(Map<String, IListQuery> listQueries) {
		this.listQueries = listQueries;
	}

	
	public List<String> getChiefAdminRoleCodes() {
		return chiefAdminRoleCodes;
	}

	public void setChiefAdminRoleCodes(List<String> chiefAdminRoleCodes) {
		this.chiefAdminRoleCodes = chiefAdminRoleCodes;
	}

	public Map<String, String[]> getChiefAdminViewAndAppCodes() {
		return chiefAdminViewAndAppCodes;
	}

	public void setChiefAdminViewAndAppCodes(Map<String, String[]> chiefAdminViewAndAppCodes) {
		this.chiefAdminViewAndAppCodes = chiefAdminViewAndAppCodes;
	}

	public String[] getGeneralAdminViewCodes() {
		return generalAdminViewCodes;
	}

	public void setGeneralAdminViewCodes(String[] generalAdminViewCodes) {
		this.generalAdminViewCodes = generalAdminViewCodes;
	}

}
